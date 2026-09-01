package com.kaditsm.auth.application.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.RefreshToken;
import com.kaditsm.auth.domain.exception.InactiveAccountException;
import com.kaditsm.auth.domain.exception.InvalidCredentialsException;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.port.in.CreateSessionUseCase;
import com.kaditsm.auth.domain.port.out.PasswordEncoderPort;
import com.kaditsm.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

import jakarta.transaction.Transactional;

import com.kaditsm.auth.domain.port.out.IdentityRepositoryPort;

@Service
@Transactional
public class CreateSessionService implements CreateSessionUseCase {

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationInMs;

    private final IdentityRepositoryPort identityRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    public CreateSessionService(IdentityRepositoryPort identityRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort) {

        this.identityRepositoryPort = identityRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    @Override
    public LoginResult createSession(CreateSessionCommand command) {
        Identity identity = identityRepositoryPort.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!identity.isActive()) {
            throw new InactiveAccountException();
        }

        if (!passwordEncoderPort.matches(command.rawPassword(), identity.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        Map<String, Object> extraClaims = Map.of("tenant_id", identity.getTenantId().toString());

        UUID refreshTokenId = UUID.randomUUID();

        refreshTokenRepositoryPort.save(new RefreshToken(refreshTokenId, identity.getId(),
                Instant.now().plusMillis(refreshTokenExpirationInMs), false, Instant.now()));

        LoginResult result = tokenProviderPort.generateTokens(identity, extraClaims, refreshTokenId);

        return result;
    }
}