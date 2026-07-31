package com.kadadana.kaditsm.modules.session.dto;

//JAVA IMPORTS
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response payload containing the JWT access token")
public class SessionResponseDTO {

    @Schema(description = "JWT access token to be used as Bearer token in subsequent requests", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Assigned user role in the system", example = "ADMIN")
    private String role;

    @Schema(description = "Unique user identifier", example = "b8a80121-8fca-187d-818f-ca187d120000")
    private UUID userId;
}