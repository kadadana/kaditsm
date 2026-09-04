package com.kaditsm.auth.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaditsm.auth.domain.event.TokenBlacklistedEvent;
import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.RefreshToken;
import com.kaditsm.auth.domain.port.in.RefreshTokenUseCase;
import com.kaditsm.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.kaditsm.auth.domain.port.out.TokenBlacklistPort;
import com.kaditsm.auth.domain.port.out.TokenEventPublisherPort;
import com.kaditsm.auth.domain.port.out.TokenParserPort;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CreateTokenService implements RefreshTokenUseCase {
    private final TokenProviderPort tokenProviderPort;
    private final TokenParserPort tokenParserPort;
    private final TokenBlacklistPort tokenBlacklistPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final TokenEventPublisherPort tokenEventPublisher;

    public CreateTokenService(
            TokenProviderPort tokenProviderPort,
            TokenParserPort tokenParserPort,
            TokenBlacklistPort tokenBlacklistPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            TokenEventPublisherPort tokenEventPublisher) {
        this.tokenProviderPort = tokenProviderPort;
        this.tokenParserPort = tokenParserPort;
        this.tokenBlacklistPort = tokenBlacklistPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.tokenEventPublisher = tokenEventPublisher;
    }

    @Override
    public LoginResult refreshToken(RefreshTokenCommand command) {

        if (!tokenParserPort.validateToken(command.refreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshToken oldRefreshToken = tokenParserPort.parseRefreshToken(command.refreshToken());

        if (oldRefreshToken.getId() == null) {
            throw new IllegalArgumentException("Invalid refresh token: missing jti");
        }

        if (oldRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token is expired");
        }

        if (tokenBlacklistPort.isBlacklisted(oldRefreshToken.getId())) {
            throw new IllegalArgumentException("Refresh token is blacklisted");
        }

        if (oldRefreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token is revoked");
        }

        var remainingTtl = Duration.between(Instant.now(), oldRefreshToken.getExpiresAt());

        var newRefreshToken = new RefreshToken(
                UUID.randomUUID(),
                oldRefreshToken.getIdentityId(),
                Instant.now().plus(remainingTtl),
                false,
                Instant.now());

        tokenBlacklistPort.blacklistToken(oldRefreshToken.getId(),
                tokenParserPort.getRemainingTtl(command.refreshToken()));

        tokenEventPublisher.publishTokenBlacklisted(
                new TokenBlacklistedEvent(
                        oldRefreshToken.getId(),
                        oldRefreshToken.getIdentityId(),
                        Instant.now(),
                        Instant.now().plus(remainingTtl)));

        refreshTokenRepositoryPort.save(newRefreshToken);

        return tokenProviderPort.generateLoginResultWithRefreshToken(newRefreshToken, null);
    }
}
