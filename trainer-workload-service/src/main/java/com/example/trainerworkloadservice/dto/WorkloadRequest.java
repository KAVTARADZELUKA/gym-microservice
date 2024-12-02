package com.example.trainerworkloadservice.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class WorkloadRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private Boolean isActive;

    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Invalid date format. Use yyyy-MM-dd")
    private String trainingDate;

    @Min(1)
    private Long duration;

    @Pattern(regexp = "ADD|DELETE", message = "ActionType must be ADD or DELETE")
    private String actionType;
}
