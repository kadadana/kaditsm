package com.kaditsm.auth.domain.port.in;

public interface RequestPasswordResetUseCase {
    void requestReset(RequestPasswordResetCommand command);

    record RequestPasswordResetCommand(String email) {}
}