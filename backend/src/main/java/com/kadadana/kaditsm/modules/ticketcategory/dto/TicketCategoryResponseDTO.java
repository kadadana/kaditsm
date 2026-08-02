package com.kadadana.kaditsm.modules.ticketcategory.dto;

//JAVA IMPORTS
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Response model for ticket category with tree structure")
public class TicketCategoryResponseDTO {
    @Schema(description = "Unique category identifier")
    private UUID id;

    @Schema(description = "Unique code for the category, e.g., 'HR' or 'IT'")
    private String code;

    @Schema(description = "Human-readable label for the category")
    private String label;

    @Schema(description = "Parent category identifier when this is a child category")
    private UUID parentId;

    @Schema(description = "Label of the parent category, if any")
    private String parentLabel;

    @Schema(description = "Child categories")
    private List<TicketCategoryResponseDTO> children;

    @Schema(description = "Whether the category is active")
    private Boolean active;
}