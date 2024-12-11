package com.example.trainerworkloadservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadMessage implements Serializable {
    private WorkloadRequest request;
    private String transactionId;
}
