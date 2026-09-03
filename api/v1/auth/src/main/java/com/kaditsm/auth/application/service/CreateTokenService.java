package com.kaditsm.auth.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.port.in.CreateTokenUseCase;
import com.kaditsm.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.kaditsm.auth.domain.port.out.TokenParserPort;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CreateTokenService implements CreateTokenUseCase {
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final TokenProviderPort tokenProviderPort;
    private final TokenParserPort tokenParserPort;

    public CreateTokenService(RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            TokenProviderPort tokenProviderPort, TokenParserPort tokenParserPort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.tokenProviderPort = tokenProviderPort;
        this.tokenParserPort = tokenParserPort;
    }

    @Override
    public LoginResult createToken(CreateTokenCommand command) {

        UUID jti = tokenParserPort.extractJti(command.refreshToken());
        
        var refreshTokenEntity = refreshTokenRepositoryPort.findById(jti)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (refreshTokenEntity.isRevoked()) {
            throw new IllegalArgumentException("Refresh token is revoked");
        }

        if (refreshTokenEntity.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Refresh token has expired");
        }

        return tokenProviderPort.generateLoginResultWithRefreshToken(refreshTokenEntity, null);
    }
}
