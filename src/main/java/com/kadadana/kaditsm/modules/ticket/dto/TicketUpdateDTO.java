package com.kadadana.kaditsm.modules.ticket.dto;

//JAVA IMPORTS
import lombok.Data;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating a ticket (partial fields allowed)")
@Data
public class TicketUpdateDTO {
    @Schema(description = "Short title of the ticket")
    private String title;

    @Schema(description = "Detailed description of the issue")
    private String description;

    @Schema(description = "Identifier of the ticket category")
    private UUID categoryId;

    @Schema(description = "Identifier of the ticket status")
    private UUID statusId;

    @Schema(description = "Identifier of the ticket urgency level")
    private UUID urgencyId;

    @Schema(description = "Identifier of the user assigned to the ticket")
    private UUID assignedUserId;
}