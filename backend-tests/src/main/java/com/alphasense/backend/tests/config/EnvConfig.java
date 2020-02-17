package com.alphasense.backend.tests.config;

/**
 * Holds environment configuration for tests
 */
public class EnvConfig {

    private Gateways gateways;

    public void setGateways(Gateways gateways) {
        this.gateways = gateways;
    }

    public String getRestGatewayUrl() {
        return gateways.getRestGatewayUrl();
    }

    public static class Gateways {

        private String restGatewayUrl;

        public String getRestGatewayUrl() {
            return restGatewayUrl;
        }

        public void setRestGatewayUrl(String restGatewayUrl) {
            this.restGatewayUrl = restGatewayUrl;
        }
    }
}