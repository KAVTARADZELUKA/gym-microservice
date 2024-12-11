package com.gym.gymsystem.service;

import com.gym.gymsystem.dto.workload.WorkloadMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageProducer {
    private final JmsTemplate jmsTemplate;

    public MessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendTo(String destination, WorkloadMessage message) {
        jmsTemplate.convertAndSend(destination, message, postProcessor -> {
            postProcessor.setStringProperty("_asb_", WorkloadMessage.class.getName());
            return postProcessor;
        });
        log.info("Producer> Message Sent");
    }
}
