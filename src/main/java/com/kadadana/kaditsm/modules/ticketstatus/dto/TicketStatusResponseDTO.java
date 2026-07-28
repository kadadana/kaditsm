package com.kadadana.kaditsm.modules.ticketstatus.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TicketStatusResponseDTO {
    private UUID id;
    private String code;
    private String label;
    private String colorCode;
    private Boolean active;
    private Boolean isDefault;
}