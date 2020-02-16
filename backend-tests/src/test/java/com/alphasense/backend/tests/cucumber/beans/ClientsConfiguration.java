package com.alphasense.backend.tests.cucumber.beans;

import com.alphasense.backend.client.core.rest.RestGatewayClient;
import com.alphasense.backend.tests.config.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsConfiguration {

    @Autowired
    private EnvConfig config;

    @Bean
    public RestGatewayClient restGatewayClient() {
        return new RestGatewayClient(config.getRestGatewayUrl());
    }

    private String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }
}