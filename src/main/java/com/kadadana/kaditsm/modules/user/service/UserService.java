package com.kadadana.kaditsm.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.kadadana.kaditsm.modules.user.dto.UserCreateDTO;
import com.kadadana.kaditsm.modules.user.dto.UserUpdateDTO;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.entity.UserEntity;
import com.kadadana.kaditsm.modules.user.mapper.UserMapper;
import com.kadadana.kaditsm.modules.user.repository.UserRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(entity -> userMapper.toResponseDTO(entity))
                .orElseThrow(() -> new RuntimeException("The user with ID " + id + " was not found."));
    }

    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(entity -> userMapper.toResponseDTO(entity))
                .orElseThrow(() -> new RuntimeException("The user with username " + username + " was not found."));
    }

    @Transactional
    public UserEntity createUser(UserCreateDTO createDTO) {
        UserEntity entity = UserEntity.builder()
                .username(createDTO.getUsername())
                .email(createDTO.getEmail())
                .role(createDTO.getRole())
                .build();

        return userRepository.save(entity);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO updateDTO) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        userMapper.updateEntityFromDto(updateDTO, entity);

        UserEntity updatedTicket = userRepository.save(entity);
        return userMapper.toResponseDTO(updatedTicket);
    }

    public List<UserResponseDTO> getUserByRole(String role) {
        return userRepository.findAllByRole(role).stream()
                .map(entity -> userMapper.toResponseDTO(entity))
                .toList();
    }
}