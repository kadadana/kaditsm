package com.kaditsm.auth.adapter.in.web.identity.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record CreateIdentityRequest(
        @NotNull(message = "Tenant ID necessary") 
        UUID tenantId,

        @NotBlank(message = "Email can not be null")
        @Email(message = "Invalid Email") 
        String email,

        @NotBlank
        @Size(min = 8, message = "The password must be at least 8 characters") 
        String password) {
}