package com.kaditsm.auth.domain.port.in;

import com.kaditsm.auth.domain.model.Identity;
import java.util.UUID;

public interface CreateIdentityUseCase {
    Identity create(CreateIdentityCommand command);

    record CreateIdentityCommand(
            UUID tenantId,
            String email,
            String rawPassword
            ) {
    }
}
