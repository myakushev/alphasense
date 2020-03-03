package com.alphasense.backend.tests.cucumber.beans;

import com.alphasense.backend.client.core.rest.RestGatewayClient;
import com.alphasense.backend.tests.config.ConfigReader;
import com.alphasense.backend.tests.config.EnvConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientsConfiguration {

    @Bean
    public EnvConfig config(){
        return ConfigReader.readEnvConfig();
    };

    @Bean
    public RestGatewayClient restGatewayClient() {
        return new RestGatewayClient(config().getRestGatewayUrl());
    }

}