package com.kaditsm.auth.adapter.out.persistence.identityrole;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "identity_roles")
public class IdentityRoleJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String role;

    protected IdentityRoleJpaEntity() {
    }

    public IdentityRoleJpaEntity(
            UUID id,
            String role) {
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
