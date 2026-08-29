package com.kaditsm.auth.domain.port.in;

import com.kaditsm.auth.domain.model.Identity;
import java.util.UUID;

public interface UpdateIdentityUseCase {
    Identity updateIdentity(UpdateIdentityCommand command);

    record UpdateIdentityCommand(
            UUID accountId,
            String email,
            String currentPassword,
            String newPassword
    ) {}
}