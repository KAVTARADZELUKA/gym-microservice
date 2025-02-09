package com.example.trainerworkloadservice.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Cucumber.class)
@DataMongoTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
