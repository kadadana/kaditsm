package com.kaditsm.project.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaditsm.project.entity.ProjectMember;
import com.kaditsm.project.entity.ProjectRole;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    Optional<ProjectMember> findByProjectIdAndUserId(UUID projectId, UUID userId);

    long countByProjectIdAndRole(UUID projectId, ProjectRole role);

    boolean existsByProjectIdAndUserId(UUID projectId, UUID userId);
}