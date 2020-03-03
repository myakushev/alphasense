package com.alphasense.backend.tests.cucumber.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfiguration {

    @Bean
    public TestContext testContext() {
        return new TestContext();
    }
}
