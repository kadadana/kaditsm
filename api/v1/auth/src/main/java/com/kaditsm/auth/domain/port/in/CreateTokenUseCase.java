package com.kaditsm.auth.domain.port.in;

import com.kaditsm.auth.domain.model.LoginResult;

public interface CreateTokenUseCase {
    LoginResult createToken(CreateTokenCommand command);

    record CreateTokenCommand(String refreshToken) {
    }
}
