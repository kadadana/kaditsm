package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.LoginResult;

import java.util.Map;
import java.util.UUID;

public interface TokenProviderPort {
    LoginResult generateTokens(Identity identity, Map<String, Object> extraClaims, UUID refreshTokenId);
}