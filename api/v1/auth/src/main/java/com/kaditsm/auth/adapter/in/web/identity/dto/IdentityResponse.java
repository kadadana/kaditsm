package com.kaditsm.auth.adapter.in.web.identity.dto;

import java.util.UUID;

public record IdentityResponse(
                UUID id,
                UUID tenantId,
                String email,
                boolean active) {
}