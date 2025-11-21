package com.metainnotech.authservice.verification.repository;

import com.metainnotech.authservice.verification.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByTokenAndUsedFalse(String token);
}
