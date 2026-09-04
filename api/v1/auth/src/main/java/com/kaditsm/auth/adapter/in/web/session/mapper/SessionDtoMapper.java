package com.kaditsm.auth.adapter.in.web.session.mapper;

import com.kaditsm.auth.adapter.in.web.session.dto.CreateSessionRequest;
import com.kaditsm.auth.adapter.in.web.session.dto.SessionResponse;
import com.kaditsm.auth.application.port.in.CreateSessionUseCase;
import com.kaditsm.auth.domain.model.LoginResult;

import org.springframework.stereotype.Component;

@Component
public class SessionDtoMapper {

    public CreateSessionUseCase.CreateSessionCommand toCommand(CreateSessionRequest request) {
        return new CreateSessionUseCase.CreateSessionCommand(request.email(), request.password());
    }

    public SessionResponse toResponse(LoginResult authToken) {
        return new SessionResponse(
                authToken.getAccessToken(),
                authToken.getRefreshToken(),
                "Bearer");
    }
}