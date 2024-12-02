package com.example.trainerworkloadservice.h2.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingSummary {
    private Integer trainingYear;
    private Integer trainingMonth;
    private Long duration;
}
