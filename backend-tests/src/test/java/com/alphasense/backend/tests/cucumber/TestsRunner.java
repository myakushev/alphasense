package com.alphasense.backend.tests.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        tags = {"@category-two"},
        features = "src/test/resources/features/",
        plugin = {"pretty", "html:target/cucumber", "json:target/cucumber.json"},
        glue = "com.alphasense.backend.tests"
)
public class TestsRunner {
}