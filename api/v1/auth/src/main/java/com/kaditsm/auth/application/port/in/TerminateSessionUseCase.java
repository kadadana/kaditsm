package com.kaditsm.auth.application.port.in;

import java.util.UUID;

public interface TerminateSessionUseCase {
    void terminateSession(UUID jti, String accessToken);
}