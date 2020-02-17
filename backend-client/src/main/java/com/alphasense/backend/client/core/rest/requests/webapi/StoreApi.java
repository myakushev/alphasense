package com.alphasense.backend.client.core.rest.requests.webapi;

import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.alphasense.backend.client.core.rest.http.Requests;
import com.alphasense.backend.client.core.rest.requests.RequestApi;

public class StoreApi extends RequestApi {

    public StoreApi(String baseUrl) {
        super(baseUrl);
    }

    public HttpResponseDecorator createOrder(String body) {
        return sendPostRequest(Requests.StoreUrls.STORE_URL, body);
    }

    public HttpResponseDecorator getOrder(String orderId) {
        return sendGetRequest(Requests.StoreUrls.STORE_URL + "/" + orderId);
    }
}
