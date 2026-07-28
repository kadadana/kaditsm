package com.kadadana.kaditsm.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User login request payload")
public class LoginRequestDTO {

    @Schema(description = "Username", example = "kadir.yilmaz")
    private String username;

    @Schema(description = "User password", example = "P@ssword123")
    private String password;
}