package com.kaditsm.auth.application.port.in;

import com.kaditsm.auth.domain.model.LoginResult;

public interface RefreshTokenUseCase {
    LoginResult refreshToken(RefreshTokenCommand command);

    record RefreshTokenCommand(String refreshToken) {
    }
}
