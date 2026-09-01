package com.kaditsm.auth.domain.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Unauthorized.");
    }
}