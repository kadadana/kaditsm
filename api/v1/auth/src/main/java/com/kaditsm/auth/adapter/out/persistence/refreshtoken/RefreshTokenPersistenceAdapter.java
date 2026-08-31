package com.kaditsm.auth.adapter.out.persistence.refreshtoken;

import com.kaditsm.auth.adapter.out.persistence.refreshtoken.mapper.RefreshTokenEntityMapper;
import com.kaditsm.auth.domain.model.RefreshToken;
import com.kaditsm.auth.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity token = refreshTokenEntityMapper.toJpaEntity(refreshToken);
        RefreshTokenJpaEntity savedToken = refreshTokenRepositoryPort.save(token);
        return refreshTokenEntityMapper.toDomain(savedToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepositoryPort.findByToken(token)
                .map(refreshTokenEntityMapper::toDomain);
    }

    @Override
    public void revoke(String token) {
        refreshTokenRepositoryPort.findByToken(token).ifPresent(entity -> {
            entity.setRevoked(true);
            refreshTokenRepositoryPort.save(entity);
        });
    }
}