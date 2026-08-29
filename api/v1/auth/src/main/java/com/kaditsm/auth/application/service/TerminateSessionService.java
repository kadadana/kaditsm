package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.event.TokenBlacklistedEvent;
import com.kaditsm.auth.domain.port.in.TerminateSessionUseCase;
import com.kaditsm.auth.domain.port.out.TokenBlacklistPort;
import com.kaditsm.auth.domain.port.out.TokenEventPublisherPort;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TerminateSessionService implements TerminateSessionUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;
    private final TokenProviderPort tokenProviderPort;
    private final TokenEventPublisherPort tokenEventPublisherPort;

    public TerminateSessionService(TokenBlacklistPort tokenBlacklistPort,
            TokenProviderPort tokenProviderPort,
            TokenEventPublisherPort tokenEventPublisherPort) {
        this.tokenBlacklistPort = tokenBlacklistPort;
        this.tokenProviderPort = tokenProviderPort;
        this.tokenEventPublisherPort = tokenEventPublisherPort;
    }

    @Override
    public void terminateSession(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            UUID identityId = tokenProviderPort.extractIdentityId(accessToken);
            UUID tenantId = tokenProviderPort.extractTenantId(accessToken);
            String jti = tokenProviderPort.extractJti(accessToken);
            Duration remainingTtl = tokenProviderPort.getRemainingTtl(accessToken);

            tokenBlacklistPort.blacklistToken(accessToken, remainingTtl);
            Instant now = Instant.now();
            TokenBlacklistedEvent event = new TokenBlacklistedEvent(
                    identityId, tenantId, jti, now, now.plus(remainingTtl));

            tokenEventPublisherPort.publishTokenBlacklisted(event);
        }
    }
}