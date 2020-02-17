package com.alphasense.backend.client.core.rest.requests.webapi;

import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.alphasense.backend.client.core.rest.http.Requests;
import com.alphasense.backend.client.core.rest.requests.RequestApi;

public class PetApi extends RequestApi {

    public PetApi(String baseUrl) {
        super(baseUrl);
    }

    public HttpResponseDecorator deletePet(String petId) {
        return sendDeleteRequest(Requests.PetUrls.PET_URL + "/" + petId);
    }

    public HttpResponseDecorator createPet(String body) {
        return sendPostRequest(Requests.PetUrls.PET_URL, body);
    }

    public HttpResponseDecorator getPet(String petId) {
        return sendGetRequest(Requests.PetUrls.PET_URL + "/" + petId);
    }
}
