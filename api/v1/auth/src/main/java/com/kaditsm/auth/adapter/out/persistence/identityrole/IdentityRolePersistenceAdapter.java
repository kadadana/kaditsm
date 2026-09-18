package com.kaditsm.auth.adapter.out.persistence.identityrole;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kaditsm.auth.adapter.out.persistence.identityrole.mapper.IdentityRoleEntityMapper;
import com.kaditsm.auth.application.port.out.IdentityRoleRepositoryPort;
import com.kaditsm.auth.domain.model.IdentityRole;

@Component 
public class IdentityRolePersistenceAdapter implements IdentityRoleRepositoryPort {

    private final IdentityRoleJpaRepository identityRoleJpaRepositoryPort;
    private final IdentityRoleEntityMapper identityRoleEntityMapper;

    public IdentityRolePersistenceAdapter(
        IdentityRoleJpaRepository identityRoleJpaRepositoryAdapter,
        IdentityRoleEntityMapper identityRoleEntityMapper
    ){
        this.identityRoleJpaRepositoryPort = identityRoleJpaRepositoryAdapter;
        this.identityRoleEntityMapper = identityRoleEntityMapper;
    }

    @Override
    public Optional<IdentityRole> findById(UUID id) {
        return identityRoleJpaRepositoryPort.findById(id)
                .map(identityRoleEntityMapper::toDomain);
    }

}
