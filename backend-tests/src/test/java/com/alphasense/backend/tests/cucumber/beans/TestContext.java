package com.alphasense.backend.tests.cucumber.beans;

import com.alphasense.backend.client.core.rest.http.HttpHeader;
import com.alphasense.backend.client.core.rest.http.HttpResponseDecorator;
import com.google.common.collect.Maps;
import io.cucumber.core.api.Scenario;
import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TestContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private HttpHeader mobileRequestHeader;
    private HttpHeader webRequestHeader;
    private HttpResponseDecorator response;
    private CookieStore cookieStore;

    // Map for storing parameters used in tests.
    // This map can be updated during test execution.
    private Map<String, Object> params = Maps.newHashMap();

    private Scenario scenario;

    public void clear() {
        params.clear();
        mobileRequestHeader = null;
        webRequestHeader = null;
        response = null;
        scenario = null;
        cookieStore = null;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getParam(String paramName) {
        if (params.get(paramName) != null) {
            return params.get(paramName).toString();
        }
        return null;
    }

    public void addParam(String name, Object value) {
        logger.info("Adding parameter '{}' with value: '{}' to context \n", name, value);
        params.put(name, value);
    }

    public void addIndexedParam(String name, Object value) {
        if (!params.containsKey(String.format("%sIndex", name))){
            params.put(String.format("%sIndex", name), 1);
        }
        logger.info("Adding parameter '{}' with value: '{}' to context \n",
                String.format("%s:%s", name, params.get(String.format("%sIndex", name))), value);
        params.put(String.format("%s:%s", name, params.get(String.format("%sIndex", name))), value);
        logger.info("Adding parameter '{}' with value: '{}' to context \n",
                String.format("%sIndex", name), Integer.parseInt(params.get(String.format("%sIndex", name)).toString()) + 1);
        params.put(String.format("%sIndex", name),
                Integer.parseInt(params.get(String.format("%sIndex", name)).toString()) + 1);
    }

    public void incrementParam(String name, Object value) {
        logger.info("Incrementing parameter '{}' with value: '{}'\n", name, value);
        if (!(value instanceof Integer)) {
            throw new IllegalArgumentException("Only integer parameters can be incremented");
        }

        Integer finalValue = (Integer) value;
        if (params.containsKey(name)) {
            finalValue = finalValue + (Integer) params.get(name);
        }

        params.put(name, finalValue);
    }

    public boolean containsParam(String name) {
        return params.containsKey(name);
    }

    public HttpHeader getMobileRequestHeader() {
        return mobileRequestHeader;
    }

    public void addMobileRequestHeader(String key, String value) {
        mobileRequestHeader = new HttpHeader(key, value);
    }

    public HttpHeader getWebRequestHeader() {
        return webRequestHeader;
    }

    public void addWebRequestHeader(String key, String value) {
        webRequestHeader = new HttpHeader(key, value);
    }

    public String getResponseBody() {
        return response.getBody();
    }

    public HttpResponseDecorator getResponse() {
        return response;
    }

    public void setResponse(HttpResponseDecorator response) {
        this.response = response;
    }

}
