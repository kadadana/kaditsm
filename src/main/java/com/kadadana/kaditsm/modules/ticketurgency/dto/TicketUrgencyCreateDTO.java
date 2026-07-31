package com.kadadana.kaditsm.modules.ticketurgency.dto;

//JAVA IMPORTS
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to create a ticket urgency level")
@Data
public class TicketUrgencyCreateDTO {
    @Schema(description = "Unique code for the urgency level, e.g., 'P1', 'P2'")
    private String code;

    @Schema(description = "Human-readable label for the urgency level")
    private String label;

    @Schema(description = "Hex color code used in UI to represent this urgency, e.g., '#FF0000'")
    private String colorCode;
}