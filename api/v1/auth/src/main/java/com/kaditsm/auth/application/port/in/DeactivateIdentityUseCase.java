package com.kaditsm.auth.application.port.in;

import java.util.UUID;

public interface DeactivateIdentityUseCase {
    void deactivate(UUID accountId);
}