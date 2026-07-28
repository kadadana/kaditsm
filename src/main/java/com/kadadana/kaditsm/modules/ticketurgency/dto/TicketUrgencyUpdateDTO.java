package com.kadadana.kaditsm.modules.ticketurgency.dto;

import lombok.Data;

@Data
public class TicketUrgencyUpdateDTO {
    private String code;
    private String label;
    private String colorCode;
    private Boolean active;
}