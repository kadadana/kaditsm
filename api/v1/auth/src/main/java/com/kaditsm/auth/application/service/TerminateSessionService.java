package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.event.TokenBlacklistedEvent;
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
    public void terminateSession(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            UUID identityId = tokenParserPort.extractIdentityId(accessToken);
            UUID tenantId = tokenParserPort.extractTenantId(accessToken);
            String jti = tokenParserPort.extractJti(accessToken);
            Duration remainingTtl = tokenParserPort.getRemainingTtl(accessToken);

            tokenBlacklistPort.blacklistToken(accessToken, remainingTtl);

            Instant now = Instant.now();
            TokenBlacklistedEvent event = new TokenBlacklistedEvent(
                    identityId, tenantId, jti, now, now.plus(remainingTtl));
            tokenEventPublisherPort.publishTokenBlacklisted(event);
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepositoryPort.revoke(refreshToken);
        }
    }
}