package com.kaditsm.auth.application.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.RefreshToken;
import com.kaditsm.auth.application.port.in.CreateSessionUseCase;
import com.kaditsm.auth.application.port.out.IdentityRepositoryPort;
import com.kaditsm.auth.application.port.out.IdentityRoleRepositoryPort;
import com.kaditsm.auth.application.port.out.PasswordEncoderPort;
import com.kaditsm.auth.application.port.out.RefreshTokenRepositoryPort;
import com.kaditsm.auth.application.port.out.TokenProviderPort;
import com.kaditsm.auth.domain.exception.InactiveAccountException;
import com.kaditsm.auth.domain.exception.InvalidCredentialsException;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.IdentityRole;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CreateSessionService implements CreateSessionUseCase {

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationInMs;

    private final IdentityRepositoryPort identityRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final IdentityRoleRepositoryPort identityRoleRepositoryPort;

    public CreateSessionService(IdentityRepositoryPort identityRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            IdentityRoleRepositoryPort identityRoleRepositoryPort) {

        this.identityRepositoryPort = identityRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.identityRoleRepositoryPort = identityRoleRepositoryPort;
    }

    @Override
    public LoginResult createSession(CreateSessionCommand command) {
        Identity identity = identityRepositoryPort.findByEmail(command.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        String role = identityRoleRepositoryPort.findById(identity.getId())
                .map(IdentityRole::getRole)
                .orElse("member");

        if (!identity.isActive()) {
            throw new InactiveAccountException("This account is inactive.");
        }

        if (!passwordEncoderPort.matches(command.rawPassword(), identity.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        Map<String, Object> extraClaims = Map.of(
                "tenant_id", identity.getTenantId().toString(),
                "role", role);

        UUID refreshTokenId = UUID.randomUUID();

        refreshTokenRepositoryPort.save(new RefreshToken(refreshTokenId, identity.getId(),
                Instant.now().plusMillis(refreshTokenExpirationInMs), false, Instant.now()));

        RefreshToken refreshTokenEntity = refreshTokenRepositoryPort.findById(refreshTokenId)
                .orElseThrow(() -> new RuntimeException("Failed to create refresh token"));

        LoginResult result = tokenProviderPort.generateLoginResultWithRefreshToken(refreshTokenEntity, extraClaims);

        return result;
    }
}