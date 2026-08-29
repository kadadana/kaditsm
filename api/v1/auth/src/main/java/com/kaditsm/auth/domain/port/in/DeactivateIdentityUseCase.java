package com.kaditsm.auth.domain.port.in;

import java.util.UUID;

public interface DeactivateIdentityUseCase {
    void deactivate(UUID accountId);
}