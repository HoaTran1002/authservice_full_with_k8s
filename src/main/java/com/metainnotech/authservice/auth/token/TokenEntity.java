package com.metainnotech.authservice.auth.token;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
public class TokenEntity {
    @Id
    private String id;

    private String userId;

    private Instant expiresAt;

    private Instant createdAt = Instant.now();
}
