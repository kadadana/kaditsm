package com.kadadana.kaditsm.modules.ticketstatus.dto;

//JAVA IMPORTS
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Response model for ticket status type")
public class TicketStatusResponseDTO {
    @Schema(description = "Unique status identifier")
    private UUID id;

    @Schema(description = "Unique code for the status, e.g., 'OPEN'")
    private String code;

    @Schema(description = "Human-readable label for the status")
    private String label;

    @Schema(description = "Hex color code used in UI to represent this status")
    private String colorCode;

    @Schema(description = "Whether the status is active")
    private Boolean active;

    @Schema(description = "Whether this status is the default for new tickets")
    private Boolean isDefault;
}