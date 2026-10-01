package com.kaditsm.project.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kaditsm.project.dto.AddMemberRequest;
import com.kaditsm.project.dto.CreateProjectRequest;
import com.kaditsm.project.dto.ProjectResponse;
import com.kaditsm.project.service.ProjectService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProjectResponse create(@Valid @RequestBody CreateProjectRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        return projectService.create(req, UUID.fromString(jwt.getSubject()));
    }

    @GetMapping
    List<ProjectResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return projectService.listForUser(UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/{key}")
    ProjectResponse get(@PathVariable String key) {
        return projectService.get(key);
    }

    @PostMapping("/{key}/members")
    @ResponseStatus(HttpStatus.CREATED)
    void addMember(@PathVariable String key, @Valid @RequestBody AddMemberRequest req) {
        projectService.addMember(key, req);
    }

    @DeleteMapping("/{key}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeMember(@PathVariable String key, @PathVariable UUID userId) {
        projectService.removeMember(key, userId);
    }

    @PatchMapping("/{key}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archive(@PathVariable String key) {
        projectService.archive(key);
    }
}