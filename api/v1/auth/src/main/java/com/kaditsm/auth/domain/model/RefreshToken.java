package com.kaditsm.auth.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class RefreshToken {
    private final UUID id;
    private final String token;
    private final UUID identityId;
    private final UUID tenantId;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant createdAt;

    public RefreshToken(UUID id, String token, UUID identityId, UUID tenantId,
            Instant expiresAt, boolean revoked, Instant createdAt) {
        this.id = id;
        this.token = token;
        this.identityId = identityId;
        this.tenantId = tenantId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken issue(UUID id, UUID identityId, UUID tenantId,
            String tokenValue, Duration ttl, Instant now) {
        return new RefreshToken(id, tokenValue, identityId, tenantId,
                now.plus(ttl), false, now);
    }

    public boolean isValid(Instant now) {
        return !revoked && now.isBefore(expiresAt);
    }

    public RefreshToken revoke() {
        return new RefreshToken(this.id, this.token, this.identityId, this.tenantId,
                this.expiresAt, true, this.createdAt);
    }

    public UUID getId() { return id; }
    public String getToken() { return token; }
    public UUID getIdentityId() { return identityId; }
    public UUID getTenantId() { return tenantId; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public Instant getCreatedAt() { return createdAt; }
}