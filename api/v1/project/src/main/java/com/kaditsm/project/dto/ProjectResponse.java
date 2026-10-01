package com.kaditsm.project.dto;

import java.time.Instant;
import java.util.UUID;

import com.kaditsm.project.entity.Project;
import com.kaditsm.project.entity.ProjectStatus;

public record ProjectResponse(UUID id, String key, String name, String description,
        UUID ownerId, ProjectStatus status, Instant createdAt) {
    public static ProjectResponse from(Project p) {
        return new ProjectResponse(p.getId(), p.getKey(), p.getName(),
                p.getDescription(), p.getOwnerId(), p.getStatus(), p.getCreatedAt());
    }
}