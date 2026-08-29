package com.kaditsm.auth.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.kaditsm.auth.domain.model.AuthToken;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.port.in.CreateSessionUseCase;
import com.kaditsm.auth.domain.port.out.PasswordEncoderPort;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

import jakarta.transaction.Transactional;

import com.kaditsm.auth.domain.port.out.IdentityRepositoryPort;


@Service
@Transactional
public class CreateSessionService implements CreateSessionUseCase {

    private final IdentityRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    public CreateSessionService(IdentityRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public AuthToken createSession(CreateSessionCommand command) {
        Identity user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (!passwordEncoderPort.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        Map<String, Object> extraClaims = Map.of(
                "tenant_id", user.getTenantId().toString());

        return tokenProviderPort.generateTokens(user, extraClaims);
    }
}