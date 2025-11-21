package com.backend.authservice.user.service;

import com.backend.authservice.user.dto.RegisterRequest;
import com.backend.authservice.user.entity.UserEntity;
import com.backend.authservice.user.event.UserRegisteredEvent;
import com.backend.authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserEntity register(RegisterRequest req) {
        userRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("Email already in use");
        });

        UserEntity user = UserEntity.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .emailVerified(false)
                .roles("ROLE_USER")
                .build();

        user = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        eventPublisher.publishEvent(new UserRegisteredEvent(this, user.getId(), user.getEmail(), token));

        return user;
    }
}
