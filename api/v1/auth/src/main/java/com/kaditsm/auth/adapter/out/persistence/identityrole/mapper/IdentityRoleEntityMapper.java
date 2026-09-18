package com.kaditsm.auth.adapter.out.persistence.identityrole.mapper;

import org.springframework.stereotype.Component;

import com.kaditsm.auth.adapter.out.persistence.identityrole.IdentityRoleJpaEntity;
import com.kaditsm.auth.domain.model.IdentityRole;

@Component
public class IdentityRoleEntityMapper {

    public IdentityRole toDomain(IdentityRoleJpaEntity identityRole) {
        if (identityRole == null) {
            return null;
        }
        return new IdentityRole(identityRole.getId(), identityRole.getRole());
    }

    public IdentityRoleJpaEntity toJpaEntity(IdentityRole domain) {
        if (domain == null) {
            return null;
        }
        return new IdentityRoleJpaEntity(domain.getId(), domain.getRole());
    }
}
