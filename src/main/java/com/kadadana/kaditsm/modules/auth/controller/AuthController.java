package com.kadadana.kaditsm.modules.auth.controller;

//JAVA IMPORTS
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

//CORE IMPORTS
import com.kadadana.kaditsm.core.model.ApiResponse;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.auth.dto.AuthResponseDTO;
import com.kadadana.kaditsm.modules.auth.dto.ChangePasswordRequestDTO;
import com.kadadana.kaditsm.modules.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and session management operations")
public class AuthController {

    private final AuthService authService;

    @PatchMapping("/{id}")
    @Operation(summary = "Change password", description = "Updates the user's password.")
    public ApiResponse<AuthResponseDTO> changePassword(
            @PathVariable("id") UUID id,
            @RequestBody ChangePasswordRequestDTO changePasswordRequest) {
        authService.updateAuth(id, changePasswordRequest);
        return ApiResponse.success(null, "Password changed successfully.");
    }
}