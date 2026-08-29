package com.kaditsm.auth.domain.port.out;

public interface EmailSenderPort {
    void sendPasswordResetEmail(String to, String resetToken);
}