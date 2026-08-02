package com.kadadana.kaditsm.modules.ticketurgency.dto;

//JAVA IMPORTS
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to update a ticket urgency level")
@Data
public class TicketUrgencyUpdateDTO {
    @Schema(description = "Unique code for the urgency level")
    private String code;

    @Schema(description = "Human-readable label for the urgency level")
    private String label;

    @Schema(description = "Hex color code used in UI to represent this urgency")
    private String colorCode;

    @Schema(description = "Whether the urgency level is active")
    private Boolean active;
}