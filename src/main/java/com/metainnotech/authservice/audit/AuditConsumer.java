package com.metainnotech.authservice.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditRepository auditRepository;

    @RabbitListener(queues = "auth.audit.queue")
    public void handle(Map<String, Object> payload) {
        AuditLog log = new AuditLog();
        log.setEventType((String) payload.getOrDefault("eventType", "unknown"));
        log.setUserId((String) payload.getOrDefault("userId", null));
        log.setDetail(payload.getOrDefault("detail", "").toString());
        auditRepository.save(log);
    }
}
