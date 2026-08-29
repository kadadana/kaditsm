package com.kaditsm.auth.adapter.out.redis;

import com.kaditsm.auth.domain.port.out.TokenBlacklistPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisTokenBlacklistAdapter implements TokenBlacklistPort {

    private static final String BLACKLIST_KEY_PREFIX = "blacklist:token:";
    private final StringRedisTemplate redisTemplate;

    public RedisTokenBlacklistAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklistToken(String token, Duration ttl) {
        String key = BLACKLIST_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, "revoked", ttl);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_KEY_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}