package com.kadadana.kaditsm.modules.ticketurgency.dto;

//JAVA IMPORTS
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Response model for ticket urgency level")
public class TicketUrgencyResponseDTO {
    @Schema(description = "Unique urgency identifier")
    private UUID id;

    @Schema(description = "Unique code for the urgency level")
    private String code;

    @Schema(description = "Human-readable label for the urgency level")
    private String label;

    @Schema(description = "Hex color code used in UI to represent this urgency")
    private String colorCode;

    @Schema(description = "Whether the urgency level is active")
    private Boolean active;
}