package com.kadadana.kaditsm.modules.auth.dto;

//JAVA IMPORTS
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User login request payload")
public class ChangePasswordRequestDTO {

    @Schema(description = "Old  password", example = "P@ssword123")
    private String oldPassword;

    @Schema(description = "New password", example = "P@ssword123")
    private String newPassword;
}