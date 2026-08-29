package com.kaditsm.auth.adapter.out.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaditsm.auth.domain.model.PasswordResetToken;
import com.kaditsm.auth.domain.port.out.PasswordResetTokenRepositoryPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class PasswordResetTokenRedisAdapter implements PasswordResetTokenRepositoryPort {

    private static final String KEY_PREFIX = "pwd_reset:";
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public PasswordResetTokenRedisAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(PasswordResetToken token) {
        try {
            // Domain modelini Redis için DTO'ya dönüştür
            RedisPasswordResetTokenDto dto = new RedisPasswordResetTokenDto(
                    token.getToken(),
                    token.getIdentityId().toString(),
                    token.getTenantId().toString(),
                    token.getExpiresAt().toEpochMilli(),
                    token.isUsed(),
                    token.getCreatedAt().toEpochMilli()
            );

            String json = objectMapper.writeValueAsString(dto);
            Duration ttl = Duration.between(Instant.now(), token.getExpiresAt());

            if (!ttl.isNegative() && !ttl.isZero()) {
                redisTemplate.opsForValue().set(KEY_PREFIX + token.getToken(), json, ttl);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing password reset token for Redis", e);
        }
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        if (json == null) {
            return Optional.empty();
        }

        try {
            RedisPasswordResetTokenDto dto = objectMapper.readValue(json, RedisPasswordResetTokenDto.class);
            PasswordResetToken domainModel = new PasswordResetToken(
                    dto.token(),
                    UUID.fromString(dto.identityId()),
                    UUID.fromString(dto.tenantId()),
                    Instant.ofEpochMilli(dto.expiresAtEpochMilli()),
                    dto.used(),
                    Instant.ofEpochMilli(dto.createdAtEpochMilli())
            );
            return Optional.of(domainModel);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing password reset token from Redis", e);
        }
    }

    @Override
    public void deleteByToken(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }

    // Redis serialization için basit DTO
    private record RedisPasswordResetTokenDto(
            String token,
            String identityId,
            String tenantId,
            long expiresAtEpochMilli,
            boolean used,
            long createdAtEpochMilli
    ) {}
}