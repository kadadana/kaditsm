package com.kaditsm.auth.domain.exception;

public class InactiveAccountException extends RuntimeException {
    public InactiveAccountException() {
        super("This account is inactive.");
    }
}