package com.kadadana.kaditsm.modules.auth.service;

import com.kadadana.kaditsm.core.security.JwtService;
import com.kadadana.kaditsm.modules.auth.dto.LoginRequestDTO;
import com.kadadana.kaditsm.modules.auth.dto.LoginResponseDTO;
import com.kadadana.kaditsm.modules.auth.entity.AuthEntity;
import com.kadadana.kaditsm.modules.auth.repository.AuthRepository;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.service.UserService;
import com.kadadana.kaditsm.modules.auth.dto.ChangePasswordRequestDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    public LoginResponseDTO authentication(LoginRequestDTO loginRequest) {

        UserResponseDTO dbUser = userService.getUserByUsername(loginRequest.getUsername());

        AuthEntity userAuth = authRepository.findById(dbUser.getId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), userAuth.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }

        String token = jwtService.generateToken(dbUser.getUsername(), dbUser.getRole(),
                userAuth.getId().toString());

        return LoginResponseDTO.builder()
                .token(token)
                .role(dbUser.getRole())
                .userId(dbUser.getId())
                .build();
    }

    public void updateAuth(UUID id, ChangePasswordRequestDTO changePasswordRequest) {

        AuthEntity userAuth = authRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), userAuth.getPassword())) {
            throw new RuntimeException("Invalid old password.");
        }

        String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        userAuth.setPassword(encodedNewPassword);
        authRepository.save(userAuth);
    }
}