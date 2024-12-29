package com.example.trainerworkloadservice.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingSummary {
    private Integer trainingYear;
    private Integer trainingMonth;
    private Long duration;
}
