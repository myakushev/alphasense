package com.alphasense.backend.client.core.rest.http;

import com.alphasense.backend.client.core.utils.JSONUtils;
import okhttp3.HttpUrl;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HttpClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String baseUrl = "";

    public HttpClient() {

    }

    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HttpClient(String host, int port) {
        this.baseUrl = new HttpUrl.Builder().scheme("http").host(host).port(port).build().url().toExternalForm();
    }

//    public HttpResponseDecorator sendRequest(
//            RequestType type, String resource, String body, SessionInfo sessionInfo, HttpHeader... headers) {
//        RequestBuilder requestBuilder = new RequestBuilder();
//        switch (type) {
//            case POST:
//                requestBuilder
//                        .createPostRequest(baseUrl + resource)
//                        .appendJsonBody(body);
//                requestBuilder.setHeader(new HttpHeader("Content-Type", ContentType.JSON));
//                break;
//            case GET:
//                requestBuilder.createGetRequest(baseUrl + resource);
//                break;
//            case PUT:
//                requestBuilder
//                        .createPutRequest(baseUrl + resource)
//                        .appendTextBody(body);
//                break;
//        }
//        Arrays.asList(headers).forEach(requestBuilder::setHeader);
//
//        HttpResponseDecorator response = sendRequest(requestBuilder.build());
//        logger.info("Received response \n{} \nwith body:\n{}", response, JSONUtils.beautifyIfJSON(response.getBody()));
//
//        return response;
//    }

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
//        Arrays.asList(headers).forEach(requestBuilder::setHeader);

        HttpResponseDecorator response = sendRequest(requestBuilder.build());
        logger.info("Received response \n{} \nwith body:\n{}", response, JSONUtils.beautifyIfJSON(response.getBody()));

        return response;
    }

    private HttpResponseDecorator sendRequest(HttpUriRequest request) {
        logger.info("Sending request: \n{}", request);
        logger.info("Headers: {}", Arrays.toString(request.getAllHeaders()));
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase entityEnclosingRequest = (HttpEntityEnclosingRequestBase) request;
            if (null != entityEnclosingRequest.getEntity()) {
                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                try {
                    entityEnclosingRequest.getEntity().writeTo(outstream);
                    String body = outstream.toString(StandardCharsets.UTF_8.name());
                    if (Arrays.stream(request.getAllHeaders()).noneMatch(this::headerContainsMediaInfo)) {
                        logger.info("Body: \n{}", JSONUtils.beautifyIfJSON(body));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create request body", e);
                }
            }
        }

        try {
            return new HttpResponseDecorator(HttpClientBuilder.create().build().execute(request));
        } catch (IOException e) {
            throw new RuntimeException("Failed to send request", e);
        }
    }

    private boolean headerContainsMediaInfo(Header h) {
        return h.getValue() != null && (h.getValue().equals("image/jpeg") || h.getValue().equals("video/mpeg"));
    }
}
