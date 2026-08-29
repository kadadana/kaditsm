package com.kaditsm.auth.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class PasswordResetToken {
    private final String token;
    private final UUID identityId;
    private final UUID tenantId;
    private final Instant expiresAt;
    private final boolean used;
    private final Instant createdAt;

    public PasswordResetToken(String token,
            UUID identityId,
            UUID tenantId,
            Instant expiresAt,
            boolean used,
            Instant createdAt) {
        this.token = token;
        this.identityId = identityId;
        this.tenantId = tenantId;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
    }

    public static PasswordResetToken issue(UUID identityId, UUID tenantId,
            String tokenValue, Duration ttl, Instant now) {
        return new PasswordResetToken(tokenValue, identityId, tenantId,
                now.plus(ttl), false, now);
    }

    public boolean isValid(Instant now) {
        return !used && now.isBefore(expiresAt);
    }

    public PasswordResetToken markUsed() {
        return new PasswordResetToken(this.token, this.identityId, this.tenantId,
                this.expiresAt, true, this.createdAt);
    }

    public String getToken() {
        return token;
    }

    public UUID getIdentityId() {
        return identityId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}