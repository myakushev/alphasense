package com.alphasense.backend.client.core.rest.requests;

import com.alphasense.backend.client.core.rest.http.HttpClient;
import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.alphasense.backend.client.core.rest.http.RequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RequestApi {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected HttpClient httpClient;

    public RequestApi(String baseUrl) {
        httpClient = new HttpClient(baseUrl);
    }

    protected HttpResponseDecorator sendGetRequest(String url) {
        return httpClient.sendRequest(
                RequestType.GET,
                url,
                null);
    }

    protected HttpResponseDecorator sendPostRequest(String url, String body) {
        return httpClient.sendRequest(
                RequestType.POST,
                url,
                body);
    }

    protected HttpResponseDecorator sendDeleteRequest(String url) {
        return httpClient.sendRequest(
                RequestType.DELETE,
                url,
                null);
    }
}
