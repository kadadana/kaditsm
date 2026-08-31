package com.kaditsm.auth.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TokenBlacklistedEvent(
        UUID identityId,
        UUID tenantId,
        String tokenJti,
        Instant blacklistedAt,
        Instant expiresAt) {
}