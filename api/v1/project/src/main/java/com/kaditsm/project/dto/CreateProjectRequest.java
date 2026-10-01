package com.kaditsm.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank @Pattern(regexp = "^[A-Z][A-Z0-9]{1,9}$") String key,
        @NotBlank @Size(max = 100) String name,
        String description) {
}