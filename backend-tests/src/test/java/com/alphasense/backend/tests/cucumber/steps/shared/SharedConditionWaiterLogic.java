package com.alphasense.backend.tests.cucumber.steps.shared;

import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.alphasense.backend.client.core.utils.ConditionWaiter;
import com.alphasense.backend.client.core.utils.DataReader;
import com.alphasense.backend.tests.assertion.Assertions;
import com.alphasense.backend.tests.config.TimeoutsSettings;
import com.alphasense.backend.tests.cucumber.beans.TestContext;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.alphasense.backend.client.core.utils.DataReader.substituteParamsInString;

public class SharedConditionWaiterLogic {

    private final TestContext testContext;

    private final TimeoutsSettings timeoutsSettings;

    public SharedConditionWaiterLogic(TestContext testContext,
            TimeoutsSettings timeoutsSettings) {
        this.testContext = testContext;
        this.timeoutsSettings = timeoutsSettings;
    }

    public void waitForSuccessResponse(boolean useStrictOrder, String expectedResponse, String param,
            Function<String, HttpResponseDecorator> apiCall) {
        ConditionWaiter.waitForSuccess(
                () -> {
                    testContext.setResponse(apiCall.apply(param));
                    Assertions.assertResponseCodeIs(testContext.getResponse(), 200);
                    assertJsonEquals(useStrictOrder, expectedResponse);
                },
                timeoutsSettings.getCheckTimeout(), timeoutsSettings.getCheckPeriod());
    }

    public void waitForSuccessResponse(boolean useStrictOrder, String expectedResponse, int param,
            Function<Integer, HttpResponseDecorator> apiCall) {
        ConditionWaiter.waitForSuccess(
                () -> {
                    testContext.setResponse(apiCall.apply(param));
                    Assertions.assertResponseCodeIs(testContext.getResponse(), 200);
                    assertJsonEquals(useStrictOrder, expectedResponse);
                },
                timeoutsSettings.getCheckTimeout(), timeoutsSettings.getCheckPeriod());
    }

    public void waitForSuccessResponse(boolean useStrictOrder, String expectedResponse,
            Supplier<HttpResponseDecorator> apiCall) {
        waitForSuccessResponse(useStrictOrder, timeoutsSettings.getCheckTimeout(), expectedResponse, apiCall);
    }

    public void waitForSuccessResponse(boolean useStrictOrder, int timeout, String expectedResponse,
            Supplier<HttpResponseDecorator> apiCall) {
        ConditionWaiter.waitForSuccess(
                () -> {
                    testContext.setResponse(apiCall.get());
                    Assertions.assertResponseCodeIs(testContext.getResponse(), 200);
                    assertJsonEquals(useStrictOrder, expectedResponse);
                },
                timeout, timeoutsSettings.getCheckPeriod());
    }

    private void assertJsonEquals(boolean useStrictOrder, String expectedResponse) {
        if (useStrictOrder) {
            Assertions.assertJSONEqualsStrictOrder(
                    DataReader.substituteParamsInString(expectedResponse, testContext.getParams()),
                    testContext.getResponseBody());
        } else {
            Assertions.assertJSONEqualsNonExtensibleOrder(
                    substituteParamsInString(expectedResponse, testContext.getParams()),
                    testContext.getResponseBody()
            );
        }
    }
}
