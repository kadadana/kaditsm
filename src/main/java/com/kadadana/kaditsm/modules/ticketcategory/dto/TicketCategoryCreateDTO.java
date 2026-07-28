package com.kadadana.kaditsm.modules.ticketcategory.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TicketCategoryCreateDTO {
    private String code;
    private String label;
    private UUID parentId;
}