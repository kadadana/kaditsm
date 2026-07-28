package com.kadadana.kaditsm.modules.user.mapper;

import org.springframework.stereotype.Component;

import com.kadadana.kaditsm.modules.user.dto.UserUpdateDTO;
import com.kadadana.kaditsm.modules.user.dto.UserResponseDTO;
import com.kadadana.kaditsm.modules.user.entity.UserEntity;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(UserEntity entity) {
        return UserResponseDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .displayName(entity.getDisplayName())
                .department(entity.getDepartment())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }

    public void updateEntityFromDto(UserUpdateDTO dto, UserEntity entity) {
        if (dto == null || entity == null)
            return;

        if (dto.getUsername() != null) {
            entity.setUsername(dto.getUsername());
        }
        if (dto.getDisplayName() != null) {
            entity.setDisplayName(dto.getDisplayName());
        }
        if (dto.getDepartment() != null) {
            entity.setDepartment(dto.getDepartment());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        }
    }
}