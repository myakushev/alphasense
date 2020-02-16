package com.alphasense.backend.client.core.rest.requests;

import com.alphasense.backend.client.core.context.AuthContext;
import com.alphasense.backend.client.core.entity.Param;
import com.alphasense.backend.client.core.rest.http.HttpClient;
import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.alphasense.backend.client.core.rest.http.RequestType;
import com.alphasense.backend.client.core.utils.DataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RequestApi {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final AuthContext context;
    protected HttpClient httpClient;

    public RequestApi(String baseUrl, AuthContext context) {
        httpClient = new HttpClient(baseUrl);
        this.context = context;
    }

    public RequestApi(String baseUrl) {
        httpClient = new HttpClient(baseUrl);
        context = null;
    }

//    protected HttpResponseDecorator sendGetRequest(String url, SessionInfo sessionInfo) {
//        return httpClient.sendRequest(
//                RequestType.GET,
//                url,
//                null,
//                sessionInfo);
//    }
//
//    protected HttpResponseDecorator sendPostRequest(String url, String body, SessionInfo sessionInfo, HttpHeader... headers) {
//        return httpClient.sendRequest(
//                RequestType.POST,
//                url,
//                body,
//                sessionInfo,
//                headers);
//    }
//
//    protected HttpResponseDecorator sendDeleteRequest(String url, String body, SessionInfo sessionInfo, HttpHeader... headers) {
//        return httpClient.sendRequest(
//                RequestType.DELETE,
//                url,
//                body,
//                sessionInfo,
//                headers);
//    }

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

    protected String substituteParamsInRequestBody(String bodyPath, Param... params) {
        return DataReader.readJson(bodyPath, params);
    }
}
