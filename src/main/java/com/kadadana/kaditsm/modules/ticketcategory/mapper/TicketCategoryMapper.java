package com.kadadana.kaditsm.modules.ticketcategory.mapper;

//JAVA IMPORTS
import org.springframework.stereotype.Component;

//MODULE IMPORTS
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryCreateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryResponseDTO;
import com.kadadana.kaditsm.modules.ticketcategory.dto.TicketCategoryUpdateDTO;
import com.kadadana.kaditsm.modules.ticketcategory.entity.TicketCategoryEntity;

@Component
public class TicketCategoryMapper {

    public TicketCategoryResponseDTO toResponseDTO(TicketCategoryEntity entity) {
        if (entity == null)
            return null;

        return TicketCategoryResponseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .label(entity.getLabel())
                .active(entity.getActive())
                .build();
    }

    public TicketCategoryEntity toEntity(TicketCategoryCreateDTO dto) {
        if (dto == null)
            return null;

        return TicketCategoryEntity.builder()
                .code(dto.getCode())
                .label(dto.getLabel())
                .active(true)
                .build();
    }

    public void updateEntityFromDto(TicketCategoryUpdateDTO dto, TicketCategoryEntity entity) {
        if (dto == null || entity == null)
            return;

        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getLabel() != null) {
            entity.setLabel(dto.getLabel());
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}