package com.kaditsm.auth.adapter.out.persistence.refreshtoken.mapper;

import com.kaditsm.auth.adapter.out.persistence.refreshtoken.RefreshTokenJpaEntity;
import com.kaditsm.auth.domain.model.RefreshToken;

import org.springframework.stereotype.Component;

@Component
public class RefreshTokenEntityMapper {

    public RefreshToken toDomain(RefreshTokenJpaEntity refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        return new RefreshToken(
                refreshToken.getId(),
                refreshToken.getToken(),
                refreshToken.getIdentityId(),
                refreshToken.getTenantId(),
                refreshToken.getExpiresAt(),
                refreshToken.isRevoked(),
                refreshToken.getCreatedAt());
    }

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }
        return new RefreshTokenJpaEntity(
                domain.getId(),
                domain.getToken(),
                domain.getIdentityId(),
                domain.getTenantId(),
                domain.getExpiresAt(),
                domain.isRevoked(),
                domain.getCreatedAt());
    }
}