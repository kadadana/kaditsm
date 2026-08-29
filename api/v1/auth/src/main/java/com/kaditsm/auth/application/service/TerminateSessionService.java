package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.port.in.TerminateSessionUseCase;
import com.kaditsm.auth.domain.port.out.TokenBlacklistPort;

import jakarta.transaction.Transactional;

import java.time.Duration;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class TerminateSessionService implements TerminateSessionUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;
    private static final Duration DEFAULT_ACCESS_TOKEN_TTL = Duration.ofMinutes(15);

    public TerminateSessionService(TokenBlacklistPort tokenBlacklistPort) {
        this.tokenBlacklistPort = tokenBlacklistPort;
    }

    @Override
    public void terminateSession(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            tokenBlacklistPort.blacklistToken(accessToken, DEFAULT_ACCESS_TOKEN_TTL);
        }
    }
}