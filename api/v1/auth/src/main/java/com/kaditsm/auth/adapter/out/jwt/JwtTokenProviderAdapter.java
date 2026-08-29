package com.kaditsm.auth.adapter.out.jwt;

import com.kaditsm.auth.domain.model.AuthToken;
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
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final SecretKey secretKey;
    private final long accessTokenExpirationInMs;
    private final long refreshTokenExpirationInMs;

    public JwtTokenProviderAdapter(
            @Value("${jwt.secret:defaultSecretKeyForDevMustBeLongEnough1234567890!}") String secret,
            @Value("${jwt.access-token-expiration-ms:900000}") long accessTokenExpirationInMs,
            @Value("${jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationInMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationInMs = accessTokenExpirationInMs;
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
    }

    @Override
    public AuthToken generateTokens(Identity user, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant accessExpiry = now.plusMillis(accessTokenExpirationInMs);
        Instant refreshExpiry = now.plusMillis(refreshTokenExpirationInMs);

        String accessToken = Jwts.builder()
                .subject(user.getId().toString())
                .claims(extraClaims)
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(accessExpiry))
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(user.getId().toString())
                .claim("tenant_id", user.getTenantId().toString())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshExpiry))
                .signWith(secretKey)
                .compact();

        return new AuthToken(accessToken, refreshToken, accessTokenExpirationInMs / 1000);
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

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}