package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.RefreshToken;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    Optional<RefreshToken> findById(UUID jti);

    void revoke(UUID jti);

    RefreshToken save(RefreshToken refreshToken);
}