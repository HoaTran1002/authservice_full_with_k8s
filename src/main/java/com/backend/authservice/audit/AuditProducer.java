package com.backend.authservice.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(Map<String, Object> payload) {
        rabbitTemplate.convertAndSend("auth.audit.exchange", "auth.audit", payload);
    }
}
