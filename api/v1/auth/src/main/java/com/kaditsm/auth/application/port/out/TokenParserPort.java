package com.kaditsm.auth.application.port.out;

import java.time.Duration;
import java.util.UUID;

import com.kaditsm.auth.domain.model.RefreshToken;

public interface TokenParserPort {
    boolean validateToken(String token);

    RefreshToken parseRefreshToken(String token);

    UUID extractIdentityId(String token);

    UUID extractJti(String token);

    Duration getRemainingTtl(String token);
}