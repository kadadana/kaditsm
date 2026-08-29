package com.kaditsm.auth.adapter.in.web.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateIdentity(
        @Email(message = "Invalid email format")
        String email,

        String currentPassword,

        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword
) {}