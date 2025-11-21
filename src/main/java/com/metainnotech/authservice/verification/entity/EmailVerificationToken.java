package com.metainnotech.authservice.verification.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "email_verification_tokens")
@Data
public class EmailVerificationToken {
    @Id
    private String token;

    private String userId;

    private Instant expiresAt;

    private boolean used = false;
}
