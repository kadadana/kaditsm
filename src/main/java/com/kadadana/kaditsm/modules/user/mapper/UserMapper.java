package com.kadadana.kaditsm.modules.user.mapper;

import org.springframework.stereotype.Component;

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
}