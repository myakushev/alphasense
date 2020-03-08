
package com.alphasense.backend.tests.cucumber;

import com.alphasense.backend.client.core.rest.RestGatewayClient;
import com.alphasense.backend.client.core.utils.RegexpUtils;
import com.alphasense.backend.tests.config.EnvConfig;
import io.cucumber.core.api.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import com.alphasense.backend.tests.cucumber.beans.TestContext;
import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CucumberHooks {

    private static final String SPLITTER = "======================";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EnvConfig config;
    private final TestContext testContext;
    private final RestGatewayClient restGatewayClient;

    public CucumberHooks(EnvConfig config, TestContext testContext,
                         RestGatewayClient restGatewayClient) {
        this.config = config;
        this.testContext = testContext;
        this.restGatewayClient = restGatewayClient;
    }

    @Before("@skipped")
    public void skipScenario(Scenario scenario) {
        logger.info("SKIP SCENARIO:{}", scenario.getName());
        Assume.assumeTrue(false);
    }

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        logger.info("{} SCENARIO '{}' {}", SPLITTER, scenario.getName(), SPLITTER);
        Collection<String> tags = scenario.getSourceTagNames();
        setDefaultContextParams();
        testContext.setScenario(scenario);
    }

    @After(order = 1)
    public void tearDown() {
        testContext.clear();
    }


    private void setDefaultContextParams() {
        RegexpUtils.REGEXPS.forEach(
                (k, v) -> testContext.addParam(k, RegexpUtils.REGEXP_PREFIX + v));
    }
}