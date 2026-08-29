package com.kaditsm.auth.domain.model;

public class LoginResult {
    private final String accessToken;
    private final String refreshToken;
    private final long expiresInSeconds;

    public LoginResult(String accessToken, String refreshToken, long expiresInSeconds) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}