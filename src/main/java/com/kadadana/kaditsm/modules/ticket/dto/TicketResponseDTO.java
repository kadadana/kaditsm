package com.kadadana.kaditsm.modules.ticket.dto;

//JAVA IMPORTS
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response model for ticket details")
public class TicketResponseDTO {
    @Schema(description = "Unique ticket identifier")
    private UUID id;

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

    @Schema(description = "Ticket creation timestamp")
    private LocalDateTime createdAt;
}