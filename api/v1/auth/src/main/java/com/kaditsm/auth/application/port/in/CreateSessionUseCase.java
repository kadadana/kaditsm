package com.kaditsm.auth.application.port.in;

import com.kaditsm.auth.domain.model.LoginResult;

public interface CreateSessionUseCase {
    LoginResult createSession(CreateSessionCommand command);

    record CreateSessionCommand(String email, String rawPassword) {}
}