package com.backend.authservice.auth.controller;

import com.backend.authservice.user.repository.UserRepository;
import com.backend.authservice.verification.entity.EmailVerificationToken;
import com.backend.authservice.verification.repository.EmailVerificationTokenRepository;
import com.backend.authservice.queue.producer.EmailProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class VerifyController {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailProducer emailProducer;

    @GetMapping
    public ResponseEntity<?> verify(@RequestParam("token") String token) {
        var opt = tokenRepository.findByTokenAndUsedFalse(token);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body("Invalid or expired token");
        EmailVerificationToken t = opt.get();
        if (t.getExpiresAt().isBefore(Instant.now())) return ResponseEntity.badRequest().body("Token expired");

        var user = userRepository.findById(t.getUserId()).orElseThrow();
        user.setEmailVerified(true);
        userRepository.save(user);
        t.setUsed(true);
        tokenRepository.save(t);

        return ResponseEntity.ok("Email verified");
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@RequestParam("email") String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        var user = userOpt.get();
        if (user.isEmailVerified()) return ResponseEntity.badRequest().body("Already verified");

        String token = UUID.randomUUID().toString();
        EmailVerificationToken t = new EmailVerificationToken();
        t.setToken(token);
        t.setUserId(user.getId());
        t.setExpiresAt(Instant.now().plusSeconds(60 * 60));
        tokenRepository.save(t);

        emailProducer.sendVerificationEmail(email, token);
        return ResponseEntity.accepted().body("Verification email resent");
    }
}
