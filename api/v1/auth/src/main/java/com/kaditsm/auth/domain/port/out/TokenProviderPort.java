package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.model.RefreshToken;

import java.util.Map;
import java.util.UUID;

public interface TokenProviderPort {
    String generateRefreshToken(Identity identity, Map<String, Object> extraClaims, UUID refreshTokenId);

    LoginResult generateLoginResultWithRefreshToken(RefreshToken refreshTokenEntity, Map<String, Object> extraClaims);
}