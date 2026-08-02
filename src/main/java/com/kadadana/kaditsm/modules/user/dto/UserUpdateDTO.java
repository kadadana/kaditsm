package com.kadadana.kaditsm.modules.user.dto;

//JAVA IMPORTS
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Request payload for updating an existing user (partial fields allowed)")
@Data
public class UserUpdateDTO {
    @Schema(description = "Unique username used to login")
    private String username;

    @Schema(description = "Role assigned to the user, e.g., 'ADMIN' or 'USER'")
    private UUID role;

    @Schema(description = "Display name shown in the UI")
    private String displayName;

    @Schema(description = "Department or team the user belongs to")
    private String department;

    @Schema(description = "User's email address")
    private String email;
}