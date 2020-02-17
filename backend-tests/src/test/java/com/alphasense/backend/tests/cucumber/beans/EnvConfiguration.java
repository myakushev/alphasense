package com.alphasense.backend.tests.cucumber.beans;

import com.alphasense.backend.tests.config.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfiguration {

    @Bean
    public EnvConfig config() {
        return ConfigReader.readEnvConfig();
    }

    @Bean
    public TestContext testContext() {
        return new TestContext();
    }
}
