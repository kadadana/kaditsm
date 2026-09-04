package com.kaditsm.auth.adapter.out.jwt;

import com.kaditsm.auth.application.port.out.JwksKeyProviderPort;
import com.kaditsm.auth.application.port.out.TokenParserPort;
import com.kaditsm.auth.domain.model.RefreshToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtTokenParserAdapter implements TokenParserPort {

    private final JwksKeyProviderPort jwkKeyProviderPort;

    public JwtTokenParserAdapter(JwksKeyProviderPort jwkKeyProviderPort) {
        this.jwkKeyProviderPort = jwkKeyProviderPort;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public RefreshToken parseRefreshToken(String token) {
        Claims claims = parseClaims(token);
        UUID jti = UUID.fromString(claims.getId());
        UUID identityId = UUID.fromString(claims.getSubject());
        Instant expiresAt = claims.getExpiration().toInstant();
        boolean revoked = claims.get("is_revoked", Boolean.class);
        Instant createdAt = claims.getIssuedAt().toInstant();

        return new RefreshToken(jti, identityId, expiresAt, revoked, createdAt);
    }

    @Override
    public UUID extractIdentityId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    @Override
    public UUID extractJti(String token) {
        return UUID.fromString(parseClaims(token).getId());
    }

    @Override
    public Duration getRemainingTtl(String token) {
        Instant expiresAt = parseClaims(token).getExpiration().toInstant();
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwkKeyProviderPort.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}