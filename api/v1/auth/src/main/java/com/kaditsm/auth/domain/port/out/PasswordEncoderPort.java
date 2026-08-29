package com.kaditsm.auth.domain.port.out;

public interface PasswordEncoderPort {
    boolean matches(String rawPassword, String encodedPassword);

    String encode(String rawPassword);
}