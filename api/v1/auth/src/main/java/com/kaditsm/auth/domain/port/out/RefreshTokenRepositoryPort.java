package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revoke(String token);
}