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
public class UpdateTraineeProfileRequest {
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    private String dateOfBirth;
    private String address;
    private Boolean isActive;
}
