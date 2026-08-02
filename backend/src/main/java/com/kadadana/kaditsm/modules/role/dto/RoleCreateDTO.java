package com.kadadana.kaditsm.modules.role.dto;

//JAVA IMPORTS
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Request payload for creating a new role")
@Data
public class RoleCreateDTO {

        @Schema(description = "Role name", example = "Admin")
        private String name;

        @Schema(description = "Role description", example = "Administrator role with full permissions")
        private String description;

        @Schema(description = "Indicates if the role is default", example = "true")
        private Boolean isDefault;
}