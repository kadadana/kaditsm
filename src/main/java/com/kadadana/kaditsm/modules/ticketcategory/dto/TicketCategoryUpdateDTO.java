package com.kadadana.kaditsm.modules.ticketcategory.dto;

import lombok.Data;

@Data
public class TicketCategoryUpdateDTO {
    private String code;
    private String label;
    private Boolean active;
}