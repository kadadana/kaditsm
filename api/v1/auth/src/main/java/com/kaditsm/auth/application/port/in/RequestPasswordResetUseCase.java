package com.kaditsm.auth.application.port.in;

public interface RequestPasswordResetUseCase {
    void requestReset(RequestPasswordResetCommand command);

    record RequestPasswordResetCommand(String email) {}
}