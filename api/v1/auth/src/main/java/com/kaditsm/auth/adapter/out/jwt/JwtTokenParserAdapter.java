package com.kaditsm.auth.adapter.out.jwt;

import com.kaditsm.auth.domain.port.out.JwksKeyProviderPort;
import com.kaditsm.auth.domain.port.out.TokenParserPort;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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