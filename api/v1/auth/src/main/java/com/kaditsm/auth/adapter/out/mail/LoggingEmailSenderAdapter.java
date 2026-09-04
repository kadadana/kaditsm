package com.kaditsm.auth.adapter.out.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kaditsm.auth.application.port.out.EmailSenderPort;

@Component
public class LoggingEmailSenderAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSenderAdapter.class);

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        log.info("[EMAIL MOCK] Password reset link sent to {}: https://kaditsm.local/reset-password?token={}", to,
                resetToken);
    }
}