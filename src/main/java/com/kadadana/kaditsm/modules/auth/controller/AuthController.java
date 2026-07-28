package com.kadadana.kaditsm.modules.auth.controller;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.modules.auth.dto.LoginRequestDTO;
import com.kadadana.kaditsm.modules.auth.dto.LoginResponseDTO;
import com.kadadana.kaditsm.modules.auth.dto.ChangePasswordRequestDTO;
import com.kadadana.kaditsm.modules.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and session management operations")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT access token upon success.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequest) {

        LoginResponseDTO responseData = authService.authentication(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(responseData, "Login successful."));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Closes the current user session.")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ApiResponse<Void> response = ApiResponse.success(null, "Session closed successfully.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Change password", description = "Updates the user's password.")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable("id") UUID id,
            @RequestBody ChangePasswordRequestDTO changePasswordRequest) {
        authService.updateAuth(id, changePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully."));
    }
}