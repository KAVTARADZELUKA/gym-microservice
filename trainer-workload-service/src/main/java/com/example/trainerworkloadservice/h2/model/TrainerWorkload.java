package com.example.trainerworkloadservice.h2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class TrainerWorkload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<TrainingSummary> trainingSummaries;
}
