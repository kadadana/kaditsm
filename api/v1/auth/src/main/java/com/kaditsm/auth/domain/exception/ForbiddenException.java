package com.kaditsm.auth.domain.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("Access denied.");
    }
}