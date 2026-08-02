package com.kadadana.kaditsm.modules.role.dto;

//JAVA IMPORTS
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating a role (partial fields allowed)")
@Data
public class RoleUpdateDTO {
    @Schema(description = "Role name", example = "Admin")
    private String name;

    @Schema(description = "Role description", example = "Administrator role with full permissions")
    private String description;

    @Schema(description = "Indicates if the role is default", example = "true")
    private Boolean isDefault;
}