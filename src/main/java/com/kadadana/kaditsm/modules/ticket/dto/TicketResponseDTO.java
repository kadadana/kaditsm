package com.kadadana.kaditsm.modules.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketResponseDTO {
    private UUID id;
    private String title;
    private String description;

    private UUID categoryId;
    private UUID statusId;
    private UUID urgencyId;
    private UUID assignedUserId;

    private LocalDateTime createdAt;
}