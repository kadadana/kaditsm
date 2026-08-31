package com.kaditsm.auth.adapter.out.persistence.refreshtoken;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_token", columnList = "token", unique = true)
})
public class RefreshTokenJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private UUID identityId;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private Instant createdAt;

    public RefreshTokenJpaEntity() {
    }

    public RefreshTokenJpaEntity(
            UUID id,
            String token,
            UUID identityId,
            UUID tenantId,
            Instant expiresAt,
            boolean revoked,
            Instant createdAt) {
        this.id = id;
        this.token = token;
        this.identityId = identityId;
        this.tenantId = tenantId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getIdentityId() {
        return identityId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setIdentityId(UUID identityId) {
        this.identityId = identityId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}