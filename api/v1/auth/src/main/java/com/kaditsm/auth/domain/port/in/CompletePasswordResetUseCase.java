package com.kaditsm.auth.domain.port.in;

public interface CompletePasswordResetUseCase {
    void completeReset(CompletePasswordResetCommand command);

    record CompletePasswordResetCommand(String tokenId, String newPassword) {}
}