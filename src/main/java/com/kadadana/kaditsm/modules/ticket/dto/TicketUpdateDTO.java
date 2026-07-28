package com.kadadana.kaditsm.modules.ticket.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TicketUpdateDTO {
    private String title;
    private String description;
    private UUID categoryId;
    private UUID statusId;
    private UUID urgencyId;
    private UUID assignedUserId;
}