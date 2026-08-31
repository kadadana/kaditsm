package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.Identity;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public interface TokenProviderPort {
    LoginResult generateTokens(Identity user, Map<String, Object> extraClaims);

    boolean validateToken(String token);

    UUID extractIdentityId(String token);

    UUID extractTenantId(String token);

    String extractEmail(String token);

    Duration getRemainingTtl(String token);

    String extractJti(String token);
}