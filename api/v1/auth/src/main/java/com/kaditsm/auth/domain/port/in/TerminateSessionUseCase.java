package com.kaditsm.auth.domain.port.in;

public interface TerminateSessionUseCase {
    void terminateSession(String sessionId, String accessToken);
}