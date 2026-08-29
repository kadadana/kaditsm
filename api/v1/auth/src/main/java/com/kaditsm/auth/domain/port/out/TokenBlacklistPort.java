package com.kaditsm.auth.domain.port.out;

import java.time.Duration;

public interface TokenBlacklistPort {
    void blacklistToken(String token, Duration ttl);

    boolean isBlacklisted(String token);
}