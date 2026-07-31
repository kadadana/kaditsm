package com.kadadana.kaditsm.modules.user.controller;

//JAVA IMPORTS
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

//CORE IMPORTS
import com.kadadana.kaditsm.core.model.ApiResponse;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.user.dto.UserUpdateDTO;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.service.UserService;
import com.kadadana.kaditsm.modules.user.entity.UserEntity;
import com.kadadana.kaditsm.modules.user.dto.UserCreateDTO;
import com.kadadana.kaditsm.modules.user.mapper.UserMapper;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users and their profiles")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @Operation(summary = "List users", description = "Retrieve a list of all users.")
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ApiResponse.success(users, "Users listed successfully.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details for the given user ID.")
    public ApiResponse<UserResponseDTO> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ApiResponse.success(user, "User details retrieved successfully.");
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Create a new user with provided information. Requires appropriate permissions.")
    public ApiResponse<UserResponseDTO> createUser(@RequestBody UserCreateDTO createDTO) {
        UserEntity createdUser = userService.createUser(createDTO);
        return ApiResponse.success(userMapper.toResponseDTO(createdUser), "User created successfully.");
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update a user", description = "Partially update user details for the given ID. Authentication required.")
    public ApiResponse<UserResponseDTO> patchUser(@PathVariable UUID id,
            @RequestBody UserUpdateDTO patchDTO,
            Authentication authentication) {

        UserResponseDTO data = userService.updateUser(id, patchDTO);
        return ApiResponse.success(data, "User updated successfully.");
    }

}