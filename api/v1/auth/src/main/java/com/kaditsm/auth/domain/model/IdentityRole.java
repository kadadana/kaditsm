package com.kaditsm.auth.domain.model;

import java.util.UUID;

public class IdentityRole {
    private UUID id;
    private String role;

    public IdentityRole(UUID id, String role) {
        this.id = id;
        this.role = role;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID identityId) {
        this.id = identityId;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
