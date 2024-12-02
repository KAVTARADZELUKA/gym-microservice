package com.example.trainerworkloadservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthSummary {
    private int month;
    private Long duration;
}
