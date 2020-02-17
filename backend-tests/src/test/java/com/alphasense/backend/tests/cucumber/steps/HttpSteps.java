package com.alphasense.backend.tests.cucumber.steps;

import com.alphasense.backend.client.core.rest.RestGatewayClient;
import com.alphasense.backend.client.core.utils.DataReader;
import com.alphasense.backend.tests.assertion.Assertions;
import com.alphasense.backend.tests.cucumber.beans.TestContext;
import com.alphasense.backend.tests.cucumber.utils.StepUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import com.alphasense.backend.tests.utils.PlaceholderProcessor;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import static com.alphasense.backend.client.core.dataModels.Pet.*;
import static com.alphasense.backend.client.core.utils.JSONUtils.isJSON;

public class HttpSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private RestGatewayClient restGatewayClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Then("check response {int}")
    public void checkResponse(int httpStatus, String jsonString) {
        checkResponse(httpStatus, jsonString, Assertions::assertJSONEqualsStrictOrder);
    }

    @Then("check create pet response {int}")
    public void checkResponse(int httpStatus) {
        Assertions.assertResponseCodeIs(testContext.getResponse(), httpStatus);
        Assert.assertEquals(jsonToPet(testContext.getResponseBody()).getStatus(), testContext.getParam("petStatus"));
    }

    @Then("check response ignore order {int}")
    public void checkResponseIgnoreOrder(int httpStatus, String jsonString) {
        checkResponse(httpStatus, jsonString, Assertions::assertJSONEqualsNonExtensibleOrder);
    }

    @Then("check response status is {int}")
    public void checkResponseStatusIs(int httpStatus) {
        Assertions.assertResponseCodeIs(testContext.getResponse(), httpStatus);
    }

    private void checkResponse(int httpStatus, String jsonString, BiConsumer<String, String> assertion) {
        Assertions.assertResponseCodeIs(testContext.getResponse(), httpStatus);
        String response = DataReader.substituteParamsInString(jsonString, testContext.getParams());
        logger.info("Comparing response bodies");
        if (isJSON(response)) {
            assertion.accept(response, testContext.getResponseBody());
        } else {
            Assert.assertEquals(jsonString, testContext.getResponseBody());
        }
    }

    @And("set parameters")
    public void setParameters(List<Map<String, String>> parameters) {
        Map<String, String> row = parameters.get(0);
        row.keySet().stream()
                .forEach(key ->
                        testContext.addParam(key, DataReader.substituteParamsInString(row.get(key), testContext.getParams())));
    }

    @And("set unique pet id in context")
    public void setPetId() {
        Random random = new Random();
        int petId;
        while (true) {
            petId = random.nextInt(Integer.MAX_VALUE);
            testContext.setResponse(restGatewayClient.petApi().getPet(Integer.toString(petId)));
            if (testContext.getResponse().getStatusCode() == 404 &&
                    testContext.getResponse().getJsonParam("$.message").toString().equals("Pet not found")) {
                testContext.addParam("petId", petId);
                break;
            }
        }
    }


    @And("set unique order id in context")
    public void setOrderId() {
        Random random = new Random();
        int orderId;
        while (true) {
            orderId = random.nextInt(Integer.MAX_VALUE);
            testContext.setResponse(restGatewayClient.storeApi().getOrder(Integer.toString(orderId)));
            if (testContext.getResponse().getStatusCode() == 404) {
                testContext.addParam("orderId", orderId);
                break;
            }
        }
    }

    @And("send create pet request")
    public void sendCreatePet(String body) {
        testContext.setResponse(restGatewayClient.petApi().
                createPet(DataReader.substituteParamsInString(body, testContext.getParams())));
    }

    @And("send create pet request with params")
    public void sendCreatePetParams(List<Map<String, String>> parametersList) {
        StepUtils.putParamsIntoContext(testContext, parametersList);
        String body = createPetFromMap(DataReader.substituteParamsInMap(parametersList.get(0), testContext.getParams()))
                .petToJson(JsonInclude.Include.NON_NULL);
        testContext.setResponse(restGatewayClient.petApi().
                createPet(body));
    }

    @And("send create order request")
    public void sendCreateOrder(String body) {
        testContext.setResponse(restGatewayClient.storeApi().
                createOrder(DataReader.substituteParamsInString(PlaceholderProcessor.replaceHoldersInValues(body), testContext.getParams())));
    }

    @And("check creation of pet with id {string}")
    public void checkPetCreation(String petId) {
        String substitutedPetId = DataReader.substituteParamsInString(petId, testContext.getParams());
        testContext.setResponse(restGatewayClient.petApi().getPet(substitutedPetId));
        Assert.assertTrue(String.format("Creation of pet with id %s was not successful. Pet was not appeared in Database.", substitutedPetId),
                testContext.getResponse().isSuccess());
        logger.info(String.format("Pet with id %s was successfully created", substitutedPetId));
    }

    @And("check creation of order with id {string}")
    public void checkOrderCreation(String orderId) {
        String substitutedOrderId = DataReader.substituteParamsInString(orderId, testContext.getParams());
        testContext.setResponse(restGatewayClient.storeApi().getOrder(substitutedOrderId));
        Assert.assertTrue(
                String.format("Creation of order with id %s was not successful. Order was not appeared in Database or it's status is not 'placed'", substitutedOrderId),
                testContext.getResponse().isSuccess() && testContext.getResponse()
                        .getJsonParam("$.status").toString().equals("placed"));
        logger.info(String.format("Order with id %s was successfully created", substitutedOrderId));
    }

    @And("send delete pet request for {string}")
    public void sendDeletePet(String petId) {
        String substitutedPetId = DataReader.substituteParamsInString(petId, testContext.getParams());
        testContext.setResponse(restGatewayClient.petApi().deletePet(substitutedPetId));
        Assert.assertTrue(String.format("Error %d was occured during deteting of pet with id %s.",
                testContext.getResponse().getStatusCode(), substitutedPetId),
                testContext.getResponse().isSuccess());
        logger.info("Pet with id {} was successfully deleted", substitutedPetId);
    }

    @And("check deleting of pet with id {string}")
    public void checkPetDeleting(String petId) {
        String substitutedPetId = DataReader.substituteParamsInString(petId, testContext.getParams());
        testContext.setResponse(restGatewayClient.petApi().getPet(substitutedPetId));
        Assert.assertFalse(String.format("Deteting of pet with id %s was not successful. Pet is already exists in Database.", substitutedPetId),
                testContext.getResponse().isSuccess());
        logger.info("Pet with id {} was successfully deleted", substitutedPetId);
    }

}