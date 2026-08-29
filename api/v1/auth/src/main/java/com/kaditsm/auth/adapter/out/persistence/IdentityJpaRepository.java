package com.kaditsm.auth.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdentityJpaRepository extends JpaRepository<IdentityJpaEntity, UUID> {
    Optional<IdentityJpaEntity> findByEmail(String email);

    Optional<IdentityJpaEntity> findByEmailAndTenantId(String email, UUID tenantId);

    Optional<IdentityJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<IdentityJpaEntity> findAllByTenantId(UUID tenantId, Pageable pageable);
}