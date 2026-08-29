package com.kaditsm.auth.domain.port.in;

import com.kaditsm.auth.domain.model.AuthToken;

public interface RefreshTokenUseCase {
    AuthToken refresh(String refreshToken);
}