package com.metainnotech.authservice.queue.producer;

import com.metainnotech.authservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendVerificationEmail(String email, String token) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("token", token);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, payload);
    }
}
