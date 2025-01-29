package com.gym.gymsystem.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/com/gym/gymsystem/integration/feature",
        glue = {
                "com.gym.gymsystem.integration.steps",
                "com.gym.gymsystem.integration"
        },
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class CucumberTestRunnerITCase {
}
