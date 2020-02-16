package com.alphasense.backend.tests.config;

import java.util.Map;

/**
 * Holds environment configuration for tests
 */
public class EnvConfig {

    private Gateways gateways;
    private TimeoutsSettings timeoutsSettings;

    public void setGateways(Gateways gateways) {
        this.gateways = gateways;
    }

    public String getRestGatewayUrl() {
        return gateways.getRestGatewayUrl();
    }

    public TimeoutsSettings getTimeoutsSettings() {
        return timeoutsSettings;
    }

    public void setTimeoutsSettings(TimeoutsSettings timeoutsSettings) {
        this.timeoutsSettings = timeoutsSettings;
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