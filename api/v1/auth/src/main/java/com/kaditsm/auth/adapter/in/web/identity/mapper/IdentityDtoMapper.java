package com.kaditsm.auth.adapter.in.web.identity.mapper;

import com.kaditsm.auth.adapter.in.web.identity.dto.IdentityResponse;
import com.kaditsm.auth.adapter.in.web.identity.dto.UpdateIdentity;
import com.kaditsm.auth.application.port.in.UpdateIdentityUseCase;
import com.kaditsm.auth.domain.model.Identity;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdentityDtoMapper {

    public UpdateIdentityUseCase.UpdateIdentityCommand toCommand(UUID accountId, UpdateIdentity request) {
        return new UpdateIdentityUseCase.UpdateIdentityCommand(
                accountId,
                request.email(),
                request.currentPassword(),
                request.newPassword());
    }

    public IdentityResponse toResponse(Identity account) {
        return new IdentityResponse(
                account.getId(),
                account.getTenantId(),
                account.getEmail(),
                account.isActive());
    }
}