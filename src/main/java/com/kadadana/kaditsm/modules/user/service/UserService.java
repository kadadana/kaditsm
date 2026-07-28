package com.kadadana.kaditsm.modules.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kadadana.kaditsm.modules.user.dto.UserPatchDTO;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.entity.UserEntity;
import com.kadadana.kaditsm.modules.user.mapper.UserMapper;
import com.kadadana.kaditsm.modules.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO getUserById(String id) {
        return userRepository.findById(id)
                .map(entity -> userMapper.toResponseDTO(entity))
                .orElseThrow(() -> new RuntimeException("The user with ID " + id + " was not found."));
    }

    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(entity -> userMapper.toResponseDTO(entity))
                .orElseThrow(() -> new RuntimeException("The user with username " + username + " was not found."));
    }

    public UserResponseDTO authenticate(String username, String rawPassword) {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

        if (entity.getPassword() == null || !passwordEncoder.matches(rawPassword, entity.getPassword())) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        return userMapper.toResponseDTO(entity);
    }

    @Transactional
    public UserResponseDTO patchUser(String id, UserPatchDTO patchDTO) {
        if (patchDTO.getRole() == null) {
            throw new IllegalArgumentException("The role to update cannot be null.");
        }

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The user with ID " + id + " was not found."));
        entity.setRole(patchDTO.getRole());
        UserEntity updated = userRepository.save(entity);
        return userMapper.toResponseDTO(updated);
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("The user with username " + username + " was not found."));

        if (entity.getPassword() == null || !passwordEncoder.matches(currentPassword, entity.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        entity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(entity);
    }

    public List<UserResponseDTO> getUserByRole(String role) {
        return userRepository.findAllByRole(role).stream()
                .map(entity -> userMapper.toResponseDTO(entity))
                .toList();
    }
}