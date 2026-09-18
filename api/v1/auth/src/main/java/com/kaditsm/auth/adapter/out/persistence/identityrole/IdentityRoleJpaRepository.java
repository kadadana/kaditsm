package com.kaditsm.auth.adapter.out.persistence.identityrole;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityRoleJpaRepository extends JpaRepository<IdentityRoleJpaEntity, UUID> {

    Optional<IdentityRoleJpaEntity> findById(UUID id);
}
