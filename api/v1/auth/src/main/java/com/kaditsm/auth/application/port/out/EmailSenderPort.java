package com.kaditsm.auth.application.port.out;

public interface EmailSenderPort {
    void sendPasswordResetEmail(String to, String resetToken);
}