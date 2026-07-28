package com.kadadana.kaditsm.modules.ticketcategory.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TicketCategoryResponseDTO {
    private UUID id;
    private String code;
    private String label;
    private UUID parentId;
    private String parentLabel;
    private List<TicketCategoryResponseDTO> children;
    private Boolean active;
}