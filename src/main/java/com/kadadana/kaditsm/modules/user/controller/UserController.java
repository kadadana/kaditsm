package com.kadadana.kaditsm.modules.user.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.modules.user.dto.UserUpdateDTO;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.service.UserService;
import com.kadadana.kaditsm.modules.user.entity.UserEntity;
import com.kadadana.kaditsm.modules.user.dto.UserCreateDTO;
import com.kadadana.kaditsm.modules.user.mapper.UserMapper;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ApiResponse.success(users, "Users listed successfully.");
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseDTO> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ApiResponse.success(user, "User details retrieved successfully.");
    }

    @PostMapping
    public ApiResponse<UserResponseDTO> createUser(@RequestBody UserCreateDTO createDTO) {
        UserEntity createdUser = userService.createUser(createDTO);
        return ApiResponse.success(userMapper.toResponseDTO(createdUser), "User created successfully.");
    }

    @PatchMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> patchUser(@PathVariable UUID id,
            @RequestBody UserUpdateDTO patchDTO,
            Authentication authentication) {

        UserResponseDTO data = userService.updateUser(id, patchDTO);
        ApiResponse<UserResponseDTO> response = ApiResponse.success(data, "User updated successfully.");
        return ResponseEntity.ok(response);
    }

}