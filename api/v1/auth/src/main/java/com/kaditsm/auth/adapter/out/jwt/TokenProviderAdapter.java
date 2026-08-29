package com.kaditsm.auth.adapter.out.jwt;

import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

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
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenProviderAdapter implements TokenProviderPort {

    private final SecretKey secretKey;
    private final long accessTokenExpirationInMs;
    private final long refreshTokenExpirationInMs;

    public TokenProviderAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationInMs,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationInMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationInMs = accessTokenExpirationInMs;
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
    }

    @Override
    public LoginResult generateTokens(Identity identity, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant accessExpiry = now.plusMillis(accessTokenExpirationInMs);
        Instant refreshExpiry = now.plusMillis(refreshTokenExpirationInMs);

        String accessToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(identity.getId().toString())
                .claims(extraClaims)
                .claim("email", identity.getEmail())
                .claim("tenant_id", identity.getTenantId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(accessExpiry))
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(identity.getId().toString())
                .claim("tenant_id", identity.getTenantId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshExpiry))
                .signWith(secretKey)
                .compact();

        return new LoginResult(accessToken, refreshToken, accessTokenExpirationInMs / 1000);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public UUID extractIdentityId(String token) {
        Claims claims = parseClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    @Override
    public Duration getRemainingTtl(String token) {
        Claims claims = parseClaims(token);
        Instant expiresAt = claims.getExpiration().toInstant();
        Duration remaining = Duration.between(Instant.now(), expiresAt);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    @Override
    public String extractJti(String token) {
        Claims claims = parseClaims(token);
        return claims.getId();
    }

    @Override
    public UUID extractTenantId(String token) {
        Claims claims = parseClaims(token);
        String tenantId = claims.get("tenant_id", String.class);
        return UUID.fromString(tenantId);
    }

    @Override
    public String extractEmail(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}