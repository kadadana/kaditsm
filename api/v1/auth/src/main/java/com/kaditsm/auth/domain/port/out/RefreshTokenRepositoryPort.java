package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revoke(String token);
}