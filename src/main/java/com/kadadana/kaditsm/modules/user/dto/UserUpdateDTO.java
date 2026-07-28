package com.kadadana.kaditsm.modules.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String username;
    private String role;
    private String displayName;
    private String department;
    private String email;
    private String password;
}