package com.gym.gymsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GymSystemApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GymSystemApplication.class);
        app.setAdditionalProfiles("local");
        app.run(args);
    }

}
