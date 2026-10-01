package com.kaditsm.project.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaditsm.project.dto.AddMemberRequest;
import com.kaditsm.project.dto.CreateProjectRequest;
import com.kaditsm.project.dto.ProjectResponse;
import com.kaditsm.project.entity.Project;
import com.kaditsm.project.entity.ProjectMember;
import com.kaditsm.project.entity.ProjectRole;
import com.kaditsm.project.entity.ProjectStatus;
import com.kaditsm.project.exception.BusinessRuleException;
import com.kaditsm.project.exception.DuplicateProjectKeyException;
import com.kaditsm.project.exception.ProjectNotFoundException;
import com.kaditsm.project.repository.ProjectMemberRepository;
import com.kaditsm.project.repository.ProjectRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projects;
    private final ProjectMemberRepository members;

    @Transactional
    public ProjectResponse create(CreateProjectRequest req, UUID currentUserId) {
        if (projects.existsByKey(req.key()))
            throw new DuplicateProjectKeyException(req.key());

        var p = new Project();
        p.setKey(req.key());
        p.setName(req.name());
        p.setDescription(req.description());
        p.setOwnerId(currentUserId);
        projects.save(p);

        var m = new ProjectMember();
        m.setProjectId(p.getId());
        m.setUserId(currentUserId);
        m.setRole(ProjectRole.ADMIN);
        members.save(m);

        return ProjectResponse.from(p);
    }

    @Transactional
    public List<ProjectResponse> listForUser(UUID userId) {
        return projects.findAllByMember(userId).stream().map(ProjectResponse::from).toList();
    }

    @Transactional
    public ProjectResponse get(String key) {
        return ProjectResponse.from(find(key));
    }

    @Transactional
    public void addMember(String key, AddMemberRequest req) {
        var p = find(key);
        if (p.getStatus() == ProjectStatus.ARCHIVED)
            throw new BusinessRuleException("Archived project");
        if (members.existsByProjectIdAndUserId(p.getId(), req.userId()))
            throw new BusinessRuleException("Already a member");

        var m = new ProjectMember();
        m.setProjectId(p.getId());
        m.setUserId(req.userId());
        m.setRole(req.role());
        members.save(m);
    }

    @Transactional
    public void removeMember(String key, UUID userId) {
        var p = find(key);
        var m = members.findByProjectIdAndUserId(p.getId(), userId)
                .orElseThrow(() -> new BusinessRuleException("Not a member"));
        if (m.getRole() == ProjectRole.ADMIN
                && members.countByProjectIdAndRole(p.getId(), ProjectRole.ADMIN) == 1)
            throw new BusinessRuleException("Cannot remove the last admin");
        members.delete(m);
    }

    @Transactional
    public void archive(String key) {
        find(key).setStatus(ProjectStatus.ARCHIVED);
    }

    private Project find(String key) {
        return projects.findByKey(key).orElseThrow(() -> new ProjectNotFoundException(key));
    }
}