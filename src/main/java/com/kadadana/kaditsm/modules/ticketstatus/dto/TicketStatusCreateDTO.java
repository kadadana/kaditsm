package com.kadadana.kaditsm.modules.ticketstatus.dto;

import lombok.Data;

@Data
public class TicketStatusCreateDTO {
    private String code;
    private String label;
    private String colorCode;
    private Boolean isDefault;
}