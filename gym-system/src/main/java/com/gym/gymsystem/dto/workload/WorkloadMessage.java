package com.gym.gymsystem.dto.workload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class WorkloadMessage  implements Serializable {
    private WorkloadRequest request;
    private String transactionId;
}
