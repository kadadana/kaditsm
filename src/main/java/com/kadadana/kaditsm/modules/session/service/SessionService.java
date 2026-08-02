package com.kadadana.kaditsm.modules.session.service;

//JAVA IMPORTS
import org.springframework.stereotype.Service;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.session.dto.LoginRequestDTO;
import com.kadadana.kaditsm.modules.session.entity.BlacklistedToken;
import com.kadadana.kaditsm.modules.session.repository.BlacklistedTokenRepository;
import com.kadadana.kaditsm.modules.session.dto.SessionResponseDTO;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

//CORE IMPORTS
import com.kadadana.kaditsm.core.security.JwtService;
import com.kadadana.kaditsm.modules.auth.dto.AuthRequestDTO;
import com.kadadana.kaditsm.modules.auth.dto.AuthResponseDTO;
//OUTER IMPORTS
import com.kadadana.kaditsm.modules.auth.service.AuthService;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.service.UserService;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final UserService userService;
    private final AuthService authService;

    public SessionResponseDTO login(LoginRequestDTO request) {
        UserResponseDTO dbUser = userService.getUserByUsername(request.getUsername());

        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername(request.getUsername());
        authRequest.setPassword(request.getPassword());

        AuthResponseDTO authResponse = authService.authentication(authRequest);
        if (authResponse == null) {
            throw new RuntimeException("Invalid credentials.");
        }

        String token = jwtService.generateToken(dbUser.getUsername(), dbUser.getRole().toString(),
                dbUser.getId().toString());

        return SessionResponseDTO.builder()
                .token(token)
                .role(dbUser.getRole().toString())
                .userId(dbUser.getId())
                .build();
    }

    public void logout(String jwt) {
        Claims claims = jwtService.parseClaims(jwt);
        String userId = claims.getSubject();
        long remainingTtlSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;

        if (remainingTtlSeconds > 0) {
            blacklistedTokenRepository.save(
                    new BlacklistedToken(jwt, userId, remainingTtlSeconds));
        }
    }

    public boolean isBlacklisted(String jwt) {
        return blacklistedTokenRepository.existsById(jwt);
    }
}
