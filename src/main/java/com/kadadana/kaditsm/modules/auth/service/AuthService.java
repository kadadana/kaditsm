package com.kadadana.kaditsm.modules.auth.service;

//JAVA IMPORTS
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.auth.dto.AuthRequestDTO;
import com.kadadana.kaditsm.modules.auth.dto.AuthResponseDTO;
import com.kadadana.kaditsm.modules.auth.entity.AuthEntity;
import com.kadadana.kaditsm.modules.auth.repository.AuthRepository;
import com.kadadana.kaditsm.modules.auth.dto.ChangePasswordRequestDTO;

//OUTER IMPORTS
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthResponseDTO authentication(AuthRequestDTO loginRequest) {

        UserResponseDTO dbUser = userService.getUserByUsername(loginRequest.getUsername());

        AuthEntity userAuth = authRepository.findById(dbUser.getId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), userAuth.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }
        return new AuthResponseDTO(
                dbUser.getRole().toString(),
                dbUser.getId());
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