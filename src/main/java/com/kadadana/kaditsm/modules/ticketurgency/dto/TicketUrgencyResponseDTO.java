package com.kadadana.kaditsm.modules.ticketurgency.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TicketUrgencyResponseDTO {
    private UUID id;
    private String code;
    private String label;
    private String colorCode;
    private Boolean active;
}