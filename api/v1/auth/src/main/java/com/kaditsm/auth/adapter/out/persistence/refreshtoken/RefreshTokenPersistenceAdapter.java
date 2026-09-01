package com.kaditsm.auth.adapter.out.persistence.refreshtoken;

import com.kaditsm.auth.adapter.out.persistence.refreshtoken.mapper.RefreshTokenEntityMapper;
import com.kaditsm.auth.domain.model.RefreshToken;
import com.kaditsm.auth.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenRepositoryPort;
    private final RefreshTokenEntityMapper refreshTokenEntityMapper;

    public RefreshTokenPersistenceAdapter(
            RefreshTokenJpaRepository refreshTokenRepositoryAdapter,
            RefreshTokenEntityMapper refreshTokenEntityMapper) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryAdapter;
        this.refreshTokenEntityMapper = refreshTokenEntityMapper;
    }

    @Override
    public Optional<RefreshToken> findById(UUID jti) {
        return refreshTokenRepositoryPort.findById(jti)
                .map(refreshTokenEntityMapper::toDomain);
    }

    @Override
    public void revoke(UUID token) {
        refreshTokenRepositoryPort.findById(token).ifPresent(entity -> {
            entity.setRevoked(true);
            refreshTokenRepositoryPort.save(entity);
        });
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entity = refreshTokenEntityMapper.toJpaEntity(refreshToken);
        RefreshTokenJpaEntity savedEntity = refreshTokenRepositoryPort.save(entity);
        return refreshTokenEntityMapper.toDomain(savedEntity);
    }
}