package com.kadadana.kaditsm.modules.ticketcategory.dto;

//JAVA IMPORTS
import lombok.Data;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to create a ticket category")
@Data
public class TicketCategoryCreateDTO {
    @Schema(description = "Unique code for the category, e.g., 'HR' or 'IT'")
    private String code;

    @Schema(description = "Human-readable label for the category")
    private String label;

    @Schema(description = "Optional parent category identifier for hierarchical categories")
    private UUID parentId;
}