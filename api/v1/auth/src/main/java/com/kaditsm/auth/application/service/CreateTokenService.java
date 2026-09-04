package com.kaditsm.auth.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaditsm.auth.application.port.in.RefreshTokenUseCase;
import com.kaditsm.auth.application.port.out.RefreshTokenRepositoryPort;
import com.kaditsm.auth.application.port.out.TokenBlacklistPort;
import com.kaditsm.auth.application.port.out.TokenEventPublisherPort;
import com.kaditsm.auth.application.port.out.TokenParserPort;
import com.kaditsm.auth.application.port.out.TokenProviderPort;
import com.kaditsm.auth.domain.event.TokenBlacklistedEvent;
import com.kaditsm.auth.domain.exception.InvalidRefreshTokenException;
import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.RefreshToken;

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
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        RefreshToken oldRefreshToken = tokenParserPort.parseRefreshToken(command.refreshToken());

        if (oldRefreshToken.getId() == null) {
            throw new InvalidRefreshTokenException("Invalid refresh token: missing jti");
        }

        if (oldRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token is expired");
        }

        if (tokenBlacklistPort.isBlacklisted(oldRefreshToken.getId())) {
            throw new InvalidRefreshTokenException("Refresh token is blacklisted");
        }

        if (oldRefreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token is revoked");
        }

        var remainingTtl = Duration.between(Instant.now(), oldRefreshToken.getExpiresAt());

        var newRefreshToken = new RefreshToken(
                UUID.randomUUID(),
                oldRefreshToken.getIdentityId(),
                Instant.now().plus(remainingTtl),
                false,
                Instant.now());
                
        tokenBlacklistPort.blacklistToken(oldRefreshToken.getId(), remainingTtl);

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
