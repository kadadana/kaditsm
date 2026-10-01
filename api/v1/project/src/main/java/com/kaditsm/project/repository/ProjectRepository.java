package com.kaditsm.project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kaditsm.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    boolean existsByKey(String key);

    Optional<Project> findByKey(String key);

    @Query("""
            select p from Project p
            where p.id in (select m.projectId from ProjectMember m where m.userId = :userId)
            """)
    List<Project> findAllByMember(UUID userId);
}