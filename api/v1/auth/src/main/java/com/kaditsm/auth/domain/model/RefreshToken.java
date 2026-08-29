package com.kaditsm.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {
    private final String token;
    private final UUID userId;
    private final Instant expiresAt;

    public RefreshToken(String token, UUID userId, Instant expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}