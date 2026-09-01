package com.kaditsm.auth.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TokenBlacklistedEvent(
        UUID tokenJti,
        UUID identityId,
        Instant blacklistedAt,
        Instant expiresAt) {
}