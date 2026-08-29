package com.kaditsm.auth.adapter.in.web.session.dto;

public record SessionResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn) {
}