package com.kaditsm.auth.adapter.in.web.passwordreset.mapper;

import com.kaditsm.auth.adapter.in.web.passwordreset.dto.CompletePasswordResetRequest;
import com.kaditsm.auth.adapter.in.web.passwordreset.dto.CreatePasswordResetTokenRequest;
import com.kaditsm.auth.application.port.in.CompletePasswordResetUseCase;
import com.kaditsm.auth.application.port.in.RequestPasswordResetUseCase;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetDtoMapper {

    public RequestPasswordResetUseCase.RequestPasswordResetCommand toCommand(CreatePasswordResetTokenRequest request) {
        return new RequestPasswordResetUseCase.RequestPasswordResetCommand(request.email());
    }

    public CompletePasswordResetUseCase.CompletePasswordResetCommand toCommand(String tokenId,
            CompletePasswordResetRequest request) {
        return new CompletePasswordResetUseCase.CompletePasswordResetCommand(tokenId, request.newPassword());
    }
}