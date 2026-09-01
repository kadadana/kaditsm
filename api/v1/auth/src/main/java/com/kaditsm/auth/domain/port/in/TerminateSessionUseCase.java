package com.kaditsm.auth.domain.port.in;

import java.util.UUID;

public interface TerminateSessionUseCase {
    void terminateSession(UUID jti, String accessToken);
}