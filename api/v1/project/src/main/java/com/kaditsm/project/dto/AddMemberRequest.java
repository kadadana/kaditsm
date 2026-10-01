package com.kaditsm.project.dto;

import java.util.UUID;

import com.kaditsm.project.entity.ProjectRole;

import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(@NotNull UUID userId, @NotNull ProjectRole role) {
}
