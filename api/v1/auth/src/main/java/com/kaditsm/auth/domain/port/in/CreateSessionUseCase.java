package com.kaditsm.auth.domain.port.in;

import com.kaditsm.auth.domain.model.AuthToken;

public interface CreateSessionUseCase {
    AuthToken createSession(CreateSessionCommand command);

    record CreateSessionCommand(String email, String rawPassword) {}
}