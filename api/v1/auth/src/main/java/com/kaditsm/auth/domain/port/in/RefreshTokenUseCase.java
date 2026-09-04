package com.kaditsm.auth.domain.port.in;

import com.kaditsm.auth.domain.model.LoginResult;

public interface RefreshTokenUseCase {
    LoginResult refreshToken(RefreshTokenCommand command);

    record RefreshTokenCommand(String refreshToken) {
    }
}
