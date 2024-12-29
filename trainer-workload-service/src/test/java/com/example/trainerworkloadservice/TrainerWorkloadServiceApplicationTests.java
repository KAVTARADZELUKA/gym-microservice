package com.example.trainerworkloadservice;

import com.example.trainerworkloadservice.mysql.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
                        "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration"
        }
)
class TrainerWorkloadServiceApplicationTests {

    @Configuration
    static class MockConfig {
        @Bean
        UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @Test
    void contextLoads() {
    }

}
