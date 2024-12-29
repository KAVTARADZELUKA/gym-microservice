package com.gym.gymsystem;

import com.gym.gymsystem.repository.TraineeRepository;
import com.gym.gymsystem.repository.TrainerRepository;
import com.gym.gymsystem.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class GymSystemApplicationTests {

    @Configuration
    static class MockConfig {
        @Bean
        TraineeRepository traineeRepository() {
            return Mockito.mock(TraineeRepository.class);
        }

        @Bean
        TrainerRepository trainerRepository() {
            return Mockito.mock(TrainerRepository.class);
        }

        @Bean
        TrainingRepository trainingRepository() {
            return Mockito.mock(TrainingRepository.class);
        }

    }

    @Test
    void contextLoads() {
    }

}
