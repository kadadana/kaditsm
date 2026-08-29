package com.kaditsm.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Identity {
    private final UUID id;
    private final UUID tenantId;
    private final String email;
    private final String passwordHash;
    private final boolean active;
    private final Instant createdAt;

    public Identity(UUID id,
            UUID tenantId,
            String email,
            String passwordHash,
            boolean active,
            Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Identity create(
            UUID tenantId, String email, String passwordHash) {
        return new Identity(
                UUID.randomUUID(),
                tenantId,
                email,
                passwordHash,
                true,
                Instant.now());
    }

    public Identity withEmail(String newEmail) {
        return new Identity(this.id,
                this.tenantId,
                newEmail,
                this.passwordHash,
                this.active,
                this.createdAt);
    }

    public Identity withPasswordHash(String newPasswordHash) {
        return new Identity(this.id,
                this.tenantId,
                this.email,
                newPasswordHash,
                this.active,
                this.createdAt);
    }

    public Identity deactivate() {
        return new Identity(this.id,
                this.tenantId,
                this.email,
                this.passwordHash,
                false,
                this.createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}