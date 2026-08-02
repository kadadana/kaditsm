package com.kadadana.kaditsm.modules.ticketcategory.dto;

//JAVA IMPORTS
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload to update a ticket category")
@Data
public class TicketCategoryUpdateDTO {
    @Schema(description = "Unique code for the category")
    private String code;

    @Schema(description = "Human-readable label for the category")
    private String label;

    @Schema(description = "Whether the category should be active")
    private Boolean active;
}