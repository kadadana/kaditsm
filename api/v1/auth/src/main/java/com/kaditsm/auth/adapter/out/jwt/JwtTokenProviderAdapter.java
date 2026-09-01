// adapter/out/jwt/JwtTokenProviderAdapter.java
package com.kaditsm.auth.adapter.out.jwt;

import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.port.out.TokenProviderPort;

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
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationInMs,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationInMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationInMs = accessTokenExpirationInMs;
        this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
    }

    @Override
    public LoginResult generateTokens(Identity identity, Map<String, Object> extraClaims, UUID refreshTokenId) {
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
                .id(refreshTokenId.toString())
                .subject(identity.getId().toString())
                .claim("is_revoked", false)
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshExpiry))
                .signWith(secretKey)
                .compact();

        return new LoginResult(accessToken, refreshToken);
    }
}