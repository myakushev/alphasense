package com.alphasense.backend.client.core.rest.http;

public class HttpHeader {

    private String name;
    private String value;

    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
