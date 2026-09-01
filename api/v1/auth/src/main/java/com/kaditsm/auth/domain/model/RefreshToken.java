package com.kaditsm.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {
    private final UUID id;
    private final UUID identityId;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant createdAt;    

    public RefreshToken(
            UUID id,
            UUID identityId,
            Instant expiresAt,
            boolean revoked,
            Instant createdAt) {
        this.id = id;
        this.identityId = identityId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken issue(
            UUID id,
            UUID identityId,
            Instant expiresAt) {
        return new RefreshToken(id, identityId, expiresAt, false, Instant.now());
    }

    public boolean isValid(Instant now) {
        return !revoked && now.isBefore(expiresAt);
    }

    public RefreshToken revoke() {
        return new RefreshToken(this.id, this.identityId, this.expiresAt, true, this.createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdentityId() {
        return identityId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }

}