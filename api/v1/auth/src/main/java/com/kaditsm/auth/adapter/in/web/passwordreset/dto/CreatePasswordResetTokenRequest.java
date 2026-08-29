package com.kaditsm.auth.adapter.in.web.passwordreset.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePasswordResetTokenRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email
) {}