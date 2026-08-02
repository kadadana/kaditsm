package com.kadadana.kaditsm.modules.user.dto;

//JAVA IMPORTS
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model containing user details")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    @Schema(description = "Unique identifier of the user")
    private UUID id;

    @Schema(description = "Unique username used to login")
    private String username;

    @Schema(description = "Display name shown in the UI")
    private String displayName;

    @Schema(description = "Department or team the user belongs to")
    private String department;

    @Schema(description = "User's email address")
    private String email;

    @Schema(description = "Role assigned to the user, e.g., 'ADMIN' or 'USER'")
    private UUID role;
}