package com.kaditsm.auth.application.port.out;

import java.time.Duration;
import java.util.UUID;

public interface TokenBlacklistPort {
    void blacklistToken(UUID jti, Duration ttl);

    boolean isBlacklisted(UUID token);
}