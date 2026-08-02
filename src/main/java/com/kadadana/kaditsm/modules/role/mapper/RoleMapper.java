package com.kadadana.kaditsm.modules.role.mapper;

//JAVA IMPORTS
import org.springframework.stereotype.Component;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.role.dto.RoleCreateDTO;
import com.kadadana.kaditsm.modules.role.dto.RoleResponseDTO;
import com.kadadana.kaditsm.modules.role.dto.RoleUpdateDTO;
import com.kadadana.kaditsm.modules.role.entity.RoleEntity;

@Component
public class RoleMapper {

    public RoleResponseDTO toResponseDTO(RoleEntity entity) {
        if (entity == null)
            return null;

        return RoleResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isDefault(entity.getIsDefault())
                .build();
    }

    public RoleEntity toEntity(RoleCreateDTO dto) {
        if (dto == null)
            return null;

        return RoleEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isDefault(dto.getIsDefault())
                .build();
    }

    public void updateEntityFromDto(RoleUpdateDTO dto, RoleEntity entity) {
        if (dto == null || entity == null)
            return;

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getIsDefault() != null) {
            entity.setIsDefault(dto.getIsDefault());
        }
    }

}