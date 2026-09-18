package com.kaditsm.auth.application.port.out;

import java.util.Optional;
import java.util.UUID;
import com.kaditsm.auth.domain.model.IdentityRole;

public interface IdentityRoleRepositoryPort {
    Optional<IdentityRole> findById(UUID id);
}