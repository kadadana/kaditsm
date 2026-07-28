package com.kadadana.kaditsm.modules.ticket.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TicketCreateDTO {
    private String title;
    private String description;
    private UUID categoryId;
    private UUID urgencyId;
}