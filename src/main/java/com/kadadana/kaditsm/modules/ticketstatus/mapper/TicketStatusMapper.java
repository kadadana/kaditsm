package com.kadadana.kaditsm.modules.ticketstatus.mapper;

import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusCreateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusResponseDTO;
import com.kadadana.kaditsm.modules.ticketstatus.dto.TicketStatusUpdateDTO;
import com.kadadana.kaditsm.modules.ticketstatus.entity.TicketStatusEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketStatusMapper {

    public TicketStatusResponseDTO toResponseDTO(TicketStatusEntity entity) {
        if (entity == null)
            return null;

        return TicketStatusResponseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .label(entity.getLabel())
                .colorCode(entity.getColorCode())
                .isDefault(entity.getIsDefault())
                .active(entity.getActive())
                .build();
    }

    public TicketStatusEntity toEntity(TicketStatusCreateDTO dto) {
        if (dto == null)
            return null;

        return TicketStatusEntity.builder()
                .code(dto.getCode())
                .label(dto.getLabel())
                .colorCode(dto.getColorCode())
                .isDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false)
                .active(true)
                .build();
    }

    public void updateEntityFromDto(TicketStatusUpdateDTO dto, TicketStatusEntity entity) {
        if (dto == null || entity == null)
            return;

        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getLabel() != null) {
            entity.setLabel(dto.getLabel());
        }
        if (dto.getColorCode() != null) {
            entity.setColorCode(dto.getColorCode());
        }
        if (dto.getIsDefault() != null) {
            entity.setIsDefault(dto.getIsDefault());
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}