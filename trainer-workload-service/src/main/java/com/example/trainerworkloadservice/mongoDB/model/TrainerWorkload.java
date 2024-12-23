package com.example.trainerworkloadservice.mongoDB.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "trainerWorkloads")
@Data
public class TrainerWorkload {
    @Id
    private String id;
    @Indexed
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private List<TrainingSummary> trainingSummaries;
}
