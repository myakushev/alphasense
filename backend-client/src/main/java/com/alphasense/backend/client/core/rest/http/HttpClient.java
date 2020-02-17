package com.alphasense.backend.client.core.rest.http;

import com.alphasense.backend.client.core.utils.JSONUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class HttpClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String baseUrl = "";

    public HttpClient() {
    }

    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HttpResponseDecorator sendRequest(
            RequestType type, String resource, String body) {
        RequestBuilder requestBuilder = new RequestBuilder();
        switch (type) {
            case GET:
                requestBuilder
                        .createGetRequest(baseUrl + resource);
                break;
            case POST:
                requestBuilder
                        .createPostRequest(baseUrl + resource)
                        .appendJsonBody(body)
                        .setHeader(new HttpHeader("Content-Type", ContentType.JSON));
                break;
            case DELETE:
                requestBuilder
                        .createDeleteRequest(baseUrl + resource);
                break;
        }
        HttpResponseDecorator response = sendRequest(requestBuilder.build());
        logger.info("Received response \n{} \nwith body:\n{}", response, JSONUtils.beautifyIfJSON(response.getBody()));

        return response;
    }

    private HttpResponseDecorator sendRequest(HttpUriRequest request) {
        logger.info("Sending request: \n{}", request);
        logger.info("Headers: {}", Arrays.toString(request.getAllHeaders()));
        try {
            return new HttpResponseDecorator(HttpClientBuilder.create().build().execute(request));
        } catch (IOException e) {
            throw new RuntimeException("Failed to send request", e);
        }
    }
}
