package com.kadadana.kaditsm.modules.ticketstatus.dto;

//JAVA IMPORTS
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to create a ticket status type")
@Data
public class TicketStatusCreateDTO {
    @Schema(description = "Unique code for the status, e.g., 'OPEN', 'CLOSED'")
    private String code;

    @Schema(description = "Human-readable label for the status")
    private String label;

    @Schema(description = "Hex color code used in UI to represent this status")
    private String colorCode;

    @Schema(description = "Whether this status should be the default for new tickets")
    private Boolean isDefault;
}