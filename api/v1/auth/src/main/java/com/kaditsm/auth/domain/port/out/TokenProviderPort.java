package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.AuthToken;
import com.kaditsm.auth.domain.model.Identity;
import java.util.Map;

public interface TokenProviderPort {
    AuthToken generateTokens(Identity user, Map<String, Object> extraClaims);

    boolean validateToken(String token);
}