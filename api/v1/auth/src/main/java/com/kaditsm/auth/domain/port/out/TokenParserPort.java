package com.kaditsm.auth.domain.port.out;

import java.time.Duration;
import java.util.UUID;

public interface TokenParserPort {
    boolean validateToken(String token);

    UUID extractIdentityId(String token);

    UUID extractJti(String token);

    Duration getRemainingTtl(String token);
}