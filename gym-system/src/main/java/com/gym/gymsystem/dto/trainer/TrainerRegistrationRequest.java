package com.gym.gymsystem.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerRegistrationRequest {
    @NotBlank(message = "firstName is required")
    String firstName;
    @NotBlank(message = "lastName is required")
    String lastName;
    @NotBlank(message = "specialization is required")
    String specialization;
}
