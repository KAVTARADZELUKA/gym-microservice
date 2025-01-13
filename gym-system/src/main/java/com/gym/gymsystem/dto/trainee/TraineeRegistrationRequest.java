package com.gym.gymsystem.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraineeRegistrationRequest {
    @NotBlank(message = "firstName is required")
    String firstName;
    @NotBlank(message = "lastName is required")
    String lastName;
    String address;
    String dateOfBirth;
}
