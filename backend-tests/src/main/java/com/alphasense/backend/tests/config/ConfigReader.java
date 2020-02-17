package com.alphasense.backend.tests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

/**
 * Reads *.yaml files with test's configuration in 'resources' folder
 */
public final class ConfigReader {

    private static final String ENV_CONFIG = "env-config.yaml";

    private ConfigReader() {
        // utils class
    }

    /**
     * Loads environment configuration for tests (connection settings; db, redis configs etc.).
     *
     * @return deserialized EnvConfig object
     */
    public static EnvConfig readEnvConfig() {
        return readConfigYAML(ENV_CONFIG, EnvConfig.class);
    }

    private static <T> T readConfigYAML(String yamlFileName, Class<T> valueType) {
        try {
            return new ObjectMapper(new YAMLFactory()).readValue(
                    ClassLoader.getSystemClassLoader().getResourceAsStream(yamlFileName),
                    valueType);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to read yaml file '%s'", yamlFileName), e);
        }
    }
}
