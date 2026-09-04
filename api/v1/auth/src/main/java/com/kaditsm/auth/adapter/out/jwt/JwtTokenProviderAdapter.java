package com.kaditsm.auth.adapter.out.jwt;

import com.kaditsm.auth.application.port.out.JwksKeyProviderPort;
import com.kaditsm.auth.application.port.out.TokenProviderPort;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.RefreshToken;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

        private final JwksKeyProviderPort jwksKeyProviderPort;
        private final long accessTokenExpirationInMs;
        private final long refreshTokenExpirationInMs;

        public JwtTokenProviderAdapter(
                        @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationInMs,
                        @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationInMs,
                        JwksKeyProviderPort jwksKeyProviderPort) {
                this.accessTokenExpirationInMs = accessTokenExpirationInMs;
                this.refreshTokenExpirationInMs = refreshTokenExpirationInMs;
                this.jwksKeyProviderPort = jwksKeyProviderPort;
        }

        @Override
        public String generateRefreshToken(Identity identity, Map<String, Object> extraClaims,
                        UUID refreshTokenId) {
                Instant now = Instant.now();
                Instant refreshExpiry = now.plusMillis(refreshTokenExpirationInMs);

                String refreshToken = Jwts.builder()
                                .id(refreshTokenId.toString())
                                .subject(identity.getId().toString())
                                .claim("is_revoked", false)
                                .issuedAt(Date.from(now))
                                .expiration(Date.from(refreshExpiry))
                                .signWith(jwksKeyProviderPort.getPrivateKey())
                                .compact();

                return refreshToken;
        }

        @Override
        public LoginResult generateLoginResultWithRefreshToken(RefreshToken refreshTokenEntity,
                        Map<String, Object> extraClaims) {
                Instant now = Instant.now();
                Instant accessExpiry = now.plusMillis(accessTokenExpirationInMs);

                String refreshToken = Jwts.builder()
                                .header()
                                .keyId(jwksKeyProviderPort.getKeyId())
                                .and()
                                .id(UUID.randomUUID().toString())
                                .subject(refreshTokenEntity.getIdentityId().toString())
                                .claim("is_revoked", refreshTokenEntity.isRevoked())
                                .issuedAt(Date.from(now))
                                .expiration(Date.from(refreshTokenEntity.getExpiresAt()))
                                .signWith(jwksKeyProviderPort.getPrivateKey())
                                .compact();

                String accessToken = Jwts.builder()
                                .header()
                                .keyId(jwksKeyProviderPort.getKeyId())
                                .and()
                                .id(refreshTokenEntity.getId().toString())
                                .subject(refreshTokenEntity.getIdentityId().toString())
                                .claims(extraClaims)
                                .issuedAt(Date.from(now))
                                .expiration(Date.from(accessExpiry))
                                .signWith(jwksKeyProviderPort.getPrivateKey())
                                .compact();

                return new LoginResult(accessToken, refreshToken);
        }
}