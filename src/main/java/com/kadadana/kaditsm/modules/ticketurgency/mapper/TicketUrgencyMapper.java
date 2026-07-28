package com.kadadana.kaditsm.modules.ticketurgency.mapper;

import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyCreateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyResponseDTO;
import com.kadadana.kaditsm.modules.ticketurgency.dto.TicketUrgencyUpdateDTO;
import com.kadadana.kaditsm.modules.ticketurgency.entity.TicketUrgencyEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketUrgencyMapper {

    public TicketUrgencyResponseDTO toResponseDTO(TicketUrgencyEntity entity) {
        if (entity == null)
            return null;

        return TicketUrgencyResponseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .label(entity.getLabel())
                .colorCode(entity.getColorCode())
                .active(entity.getActive())
                .build();
    }

    public TicketUrgencyEntity toEntity(TicketUrgencyCreateDTO dto) {
        if (dto == null)
            return null;

        return TicketUrgencyEntity.builder()
                .code(dto.getCode())
                .label(dto.getLabel())
                .colorCode(dto.getColorCode())
                .active(true)
                .build();
    }

    public void updateEntityFromDto(TicketUrgencyUpdateDTO dto, TicketUrgencyEntity entity) {
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
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}