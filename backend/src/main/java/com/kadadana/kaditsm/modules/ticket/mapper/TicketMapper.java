package com.kadadana.kaditsm.modules.ticket.mapper;

//JAVA IMPORTS
import org.springframework.stereotype.Component;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticket.dto.TicketResponseDTO;
import com.kadadana.kaditsm.modules.ticket.dto.TicketUpdateDTO;
import com.kadadana.kaditsm.modules.ticket.entity.TicketEntity;

@Component
public class TicketMapper {

    public TicketResponseDTO toResponseDTO(TicketEntity entity) {
        if (entity == null)
            return null;

        return TicketResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .categoryId(entity.getCategoryId())
                .statusId(entity.getStatusId())
                .urgencyId(entity.getUrgencyId())
                .assignedUserId(entity.getAssignedUserId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(TicketUpdateDTO dto, TicketEntity entity) {
        if (dto == null || entity == null)
            return;

        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getCategoryId() != null) {
            entity.setCategoryId(dto.getCategoryId());
        }
        if (dto.getStatusId() != null) {
            entity.setStatusId(dto.getStatusId());
        }
        if (dto.getUrgencyId() != null) {
            entity.setUrgencyId(dto.getUrgencyId());
        }
        if (dto.getAssignedUserId() != null) {
            entity.setAssignedUserId(dto.getAssignedUserId());
        }
    }
}