package com.kaditsm.auth.domain.port.out;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenPort {
    void saveResetToken(String tokenId, UUID accountId, Duration ttl);
    Optional<UUID> findAccountIdByToken(String tokenId);
    void invalidateResetToken(String tokenId);
}