package com.kaditsm.auth.application.port.out;

import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.PagedResult;

import java.util.Optional;
import java.util.UUID;

public interface IdentityRepositoryPort {
    Optional<Identity> findByEmail(String email);
    Optional<Identity> findById(UUID id);
    Optional<Identity> findByEmailAndTenantId(String email, UUID tenantId);
    Identity save(Identity account);
    PagedResult<Identity> findAllByTenantId(UUID tenantId, int page, int size);
}