package com.example.trainerworkloadservice.service;

import com.example.trainerworkloadservice.dto.WorkloadMessage;
import com.example.trainerworkloadservice.dto.WorkloadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkloadConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TrainerWorkloadService service;
    private final JmsTemplate jmsTemplate;
    private static final Logger logger = LoggerFactory.getLogger(WorkloadConsumer.class);

    public WorkloadConsumer(TrainerWorkloadService service, JmsTemplate jmsTemplate) {
        this.service = service;
        this.jmsTemplate = jmsTemplate;
    }

    @Transactional
    @JmsListener(destination = "${activemq.destination}")
    public void receiveWorkloadMessage(String message) {
        try {
            logger.info(message);
            WorkloadMessage workloadMessage = objectMapper.readValue(message, WorkloadMessage.class);

            WorkloadRequest request = workloadMessage.getRequest();
            String transactionId = workloadMessage.getTransactionId();

            service.updateWorkload(transactionId, request);

        } catch (Exception e) {
            sendToDeadLetterQueue(message);
            logger.error("Failed to parse message: {}", e.getMessage());
        }
    }

    private void sendToDeadLetterQueue(String message) {
        jmsTemplate.convertAndSend("workloadDLQ", message);
    }
}
