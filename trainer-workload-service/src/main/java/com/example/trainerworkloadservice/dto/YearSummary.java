package com.example.trainerworkloadservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class YearSummary {
    private int year;
    private List<MonthSummary> months;
}
