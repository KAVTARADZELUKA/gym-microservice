package com.example.trainerworkloadservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerMonthlySummaryResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private List<YearSummary> years;
}
