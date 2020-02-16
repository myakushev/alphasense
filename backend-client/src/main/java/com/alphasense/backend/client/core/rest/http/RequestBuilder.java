package com.alphasense.backend.client.core.rest.http;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;


public class RequestBuilder {

    private HttpUriRequest request;
    private String body = null;

    public RequestBuilder() {
    }

    public RequestBuilder createPostRequest(String url) {
        request = new HttpPost(url);
        return this;
    }

    public RequestBuilder appendJsonBody(String json) {
        body = json;
        return appendBody(new StringEntity(json, ContentType.CONTENT_TYPE_JSON));
    }

    public RequestBuilder createPutRequest(String url) {
        request = new HttpPut(url);
        return this;
    }

    public RequestBuilder appendTextBody(String text) {
        body = text;
        return appendBody(new StringEntity(text, StandardCharsets.UTF_8));
    }

    public RequestBuilder setHeader(HttpHeader header) {
        request.setHeader(header.getName(), header.getValue());
        return this;
    }

    public RequestBuilder createGetRequest(String url) {
        request = new HttpGet(url);
        return this;
    }

    public RequestBuilder createDeleteRequest(String url) {
        request = new HttpDelete(url);
        return this;
    }


    public HttpUriRequest build() {
        return request;
    }

    private RequestBuilder appendBody(StringEntity entity) {
        if (!(request instanceof HttpEntityEnclosingRequestBase)) {
            throw new IllegalArgumentException(
                    String.format("RequestApi of type '%s' can't have body", request.getClass().getName()));
        }
        ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        return this;
    }
}
