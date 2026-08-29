package com.kaditsm.auth.adapter.in.web.passwordreset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompletePasswordResetRequest(
        @NotBlank(message = "New password cannot be blank")
        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword
) {}