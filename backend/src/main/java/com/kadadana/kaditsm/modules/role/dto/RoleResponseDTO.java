package com.kadadana.kaditsm.modules.role.dto;

//JAVA IMPORTS
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response model for role details")
public class RoleResponseDTO {
    @Schema(description = "Unique role identifier")
    private UUID id;

    @Schema(description = "Role name", example = "Admin")
    private String name;

    @Schema(description = "Role description", example = "Administrator role with full permissions")
    private String description;

    @Schema(description = "Indicates if the role is default", example = "true")
    private Boolean isDefault;

}
