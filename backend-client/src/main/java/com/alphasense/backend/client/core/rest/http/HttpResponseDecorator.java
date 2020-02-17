package com.alphasense.backend.client.core.rest.http;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.apache.http.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpResponseDecorator {

    private HttpResponse responseBase;
    private String body;
    private Configuration conf = Configuration.defaultConfiguration().setOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    public HttpResponseDecorator(HttpResponse responseBase) {
        this.responseBase = responseBase;
    }

    public boolean isSuccess() {
        return getStatusCode() >= 200 && getStatusCode() <= 299;
    }

    public int getStatusCode() {
        return responseBase.getStatusLine().getStatusCode();
    }

    public <T> T getJsonParam(String paramPath) {
        return JsonPath.using(conf).parse(getBody()).read(paramPath);
    }

    public String getBody() {
        if (body == null) {
            body = "";
            if (responseBase.getEntity() != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    responseBase.getEntity().writeTo(outputStream);
                } catch (IOException e) {
                    throw new RuntimeException("Exception during response body reading", e);
                }
                body = outputStream.toString();
            }
        }
        return body;
    }

    @Override
    public String toString() {
        return responseBase.toString();
    }
}
