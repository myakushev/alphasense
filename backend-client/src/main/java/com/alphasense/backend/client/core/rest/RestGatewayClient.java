package com.alphasense.backend.client.core.rest;

import com.alphasense.backend.client.core.rest.requests.webapi.*;

public class RestGatewayClient {

    private PetApi petApi;
    private StoreApi storeApi;

    public RestGatewayClient(String baseUrl) {
        petApi = new PetApi(baseUrl);
        storeApi = new StoreApi(baseUrl);
    }

    public PetApi petApi() {
        return petApi;
    }

    public StoreApi storeApi() {
        return storeApi;
    }
}
