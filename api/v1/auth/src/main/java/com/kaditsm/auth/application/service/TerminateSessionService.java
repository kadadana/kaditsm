package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.event.TokenBlacklistedEvent;
import com.kaditsm.auth.domain.exception.ForbiddenException;
import com.kaditsm.auth.domain.exception.ResourceNotFoundException;
import com.kaditsm.auth.domain.exception.UnauthorizedException;
import com.kaditsm.auth.domain.model.RefreshToken;
import com.kaditsm.auth.domain.port.in.TerminateSessionUseCase;
import com.kaditsm.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.kaditsm.auth.domain.port.out.TokenBlacklistPort;
import com.kaditsm.auth.domain.port.out.TokenEventPublisherPort;
import com.kaditsm.auth.domain.port.out.TokenParserPort;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TerminateSessionService implements TerminateSessionUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;
    private final TokenParserPort tokenParserPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final TokenEventPublisherPort tokenEventPublisherPort;

    public TerminateSessionService(TokenBlacklistPort tokenBlacklistPort,
            TokenParserPort tokenParserPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            TokenEventPublisherPort tokenEventPublisherPort) {
        this.tokenBlacklistPort = tokenBlacklistPort;
        this.tokenParserPort = tokenParserPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.tokenEventPublisherPort = tokenEventPublisherPort;
    }

    @Override
    public void terminateSession(UUID jti, String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new UnauthorizedException();
        }



        UUID identityId = tokenParserPort.extractIdentityId(accessToken);

        RefreshToken token = refreshTokenRepositoryPort.findById(jti)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (!token.getIdentityId().equals(identityId)) {
            throw new ForbiddenException();
        }

        refreshTokenRepositoryPort.revoke(jti);

        Duration remainingTtl = tokenParserPort.getRemainingTtl(accessToken);
        tokenBlacklistPort.blacklistToken(jti, remainingTtl);

        tokenEventPublisherPort.publishTokenBlacklisted(
                new TokenBlacklistedEvent(jti, identityId, Instant.now(),
                        Instant.now().plus(remainingTtl)));

    }
}