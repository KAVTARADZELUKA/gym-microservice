package com.example.trainerworkloadservice.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/com/example/trainerworkloadservice/integration/feature",
        glue = {
                "com.example.trainerworkloadservice.integration.steps",
                "com.example.trainerworkloadservice.integration"
        },
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class CucumberTestRunnerITCase {
}
