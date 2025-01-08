package com.example.trainerworkloadservice.integration;

import com.example.trainerworkloadservice.TrainerWorkloadServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainerWorkloadServiceApplication.class)
public class CucumberSpringConfiguration {
}
