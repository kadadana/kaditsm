package com.kadadana.kaditsm.modules.user.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.kadadana.kaditsm.core.model.ApiResponse;
import com.kadadana.kaditsm.modules.user.dto.UserPatchDTO;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> patchUser(@PathVariable String id,
            @RequestBody UserPatchDTO patchDTO,
            Authentication authentication) {

        UserResponseDTO data = userService.patchUser(id, patchDTO);
        ApiResponse<UserResponseDTO> response = ApiResponse.success(data, "User updated successfully.");
        return ResponseEntity.ok(response);
    }

}