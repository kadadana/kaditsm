package com.kaditsm.project.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String key) {
        super("Project not found: " + key);
    }
}