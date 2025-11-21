package com.metainnotech.authservice.queue.consumer;

import com.metainnotech.authservice.config.RabbitMQConfig;
import com.metainnotech.authservice.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handle(Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String token = (String) payload.get("token");
        emailService.sendVerificationEmail(email, token);
    }
}
