package com.kaditsm.auth.adapter.out.persistence.mapper;

import com.kaditsm.auth.adapter.out.persistence.IdentityJpaEntity;
import com.kaditsm.auth.domain.model.Identity;
import org.springframework.stereotype.Component;

@Component
public class IdentityEntityMapper {

    public Identity toDomain(IdentityJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Identity(
                entity.getId(),
                entity.getTenantId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.isActive(),
                entity.getCreatedAt());
    }

    public IdentityJpaEntity toJpaEntity(Identity domain) {
        if (domain == null) {
            return null;
        }
        return new IdentityJpaEntity(
                domain.getId(),
                domain.getTenantId(),
                domain.getEmail(),
                domain.getPasswordHash(),
                domain.isActive(),
                domain.getCreatedAt());
    }
}