package com.example.trainerworkloadservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrainerWorkloadServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TrainerWorkloadServiceApplication.class);
        app.setAdditionalProfiles("local");
        app.run(args);
    }

}
