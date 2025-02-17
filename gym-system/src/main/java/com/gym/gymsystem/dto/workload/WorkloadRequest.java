package com.gym.gymsystem.dto.workload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkloadRequest implements Serializable {
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
