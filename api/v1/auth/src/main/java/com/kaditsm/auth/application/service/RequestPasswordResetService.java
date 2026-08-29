package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.model.PasswordResetToken;
import com.kaditsm.auth.domain.port.in.RequestPasswordResetUseCase;
import com.kaditsm.auth.domain.port.out.EmailSenderPort;
import com.kaditsm.auth.domain.port.out.IdentityRepositoryPort;
import com.kaditsm.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    private final IdentityRepositoryPort identityRepository;
    private final PasswordResetTokenRepositoryPort tokenRepository;
    private final TokenProviderPort tokenProvider;
    private final EmailSenderPort emailSender;

    public RequestPasswordResetService(IdentityRepositoryPort identityRepository,
            PasswordResetTokenRepositoryPort tokenRepository,
            TokenProviderPort tokenProvider,
            EmailSenderPort emailSender) {
        this.identityRepository = identityRepository;
        this.tokenRepository = tokenRepository;
        this.tokenProvider = tokenProvider;
        this.emailSender = emailSender;
    }

    @Override
    public void requestReset(RequestPasswordResetCommand command) {
        identityRepository.findByEmailAndTenantId(command.email(), UUID.randomUUID())
                .ifPresent(identity -> {
                    String tokenValue = tokenProvider.generateTokens(identity, null).getAccessToken();
                    PasswordResetToken resetToken = PasswordResetToken.issue(
                            identity.getId(), identity.getTenantId(), tokenValue, TOKEN_TTL, Instant.now());
                    tokenRepository.save(resetToken);
                    emailSender.sendPasswordResetEmail(identity.getEmail(), tokenValue);
                });
    }
}