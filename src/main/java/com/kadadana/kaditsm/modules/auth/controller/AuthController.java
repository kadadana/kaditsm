package com.kadadana.kaditsm.modules.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.core.security.JwtService;
import com.kadadana.kaditsm.modules.auth.dto.LoginRequestDTO;
import com.kadadana.kaditsm.modules.auth.dto.LoginResponseDTO;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequest) {

        UserResponseDTO dbUser = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        String token = jwtService.generateToken(dbUser.getUsername(), dbUser.getRole(), dbUser.getId());

        LoginResponseDTO responseData = new LoginResponseDTO(token, dbUser.getRole(),
                dbUser.getId());
        return ResponseEntity.ok(ApiResponse.success(responseData, "Login successful."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ApiResponse<Void> response = ApiResponse.success(null, "Session closed successfully.");
        return ResponseEntity.ok(response);
    }

}