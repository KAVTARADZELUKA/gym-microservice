package com.example.trainerworkloadservice.service;

import com.example.trainerworkloadservice.dto.WorkloadMessage;
import com.example.trainerworkloadservice.dto.WorkloadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkloadConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TrainerWorkloadService service;

    public WorkloadConsumer(TrainerWorkloadService service) {
        this.service = service;
    }

    @Transactional
    @JmsListener(destination = "${activemq.destination}")
    public void receiveWorkloadMessage(String message) {
        try {
            System.out.println(message);
            WorkloadMessage workloadMessage = objectMapper.readValue(message, WorkloadMessage.class);

            WorkloadRequest request = workloadMessage.getRequest();
            String transactionId = workloadMessage.getTransactionId();

            service.updateWorkload(transactionId, request);

        } catch (Exception e) {
            System.err.println("Failed to parse message: " + e.getMessage());
        }
    }
}
