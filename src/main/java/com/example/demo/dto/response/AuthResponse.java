package com.example.demo.dto.response;

public class AuthResponse {
    private String accessToken; private String refreshToken;
    private String tokenType; private Long expiresIn;
    private UserResponse user;

    public AuthResponse() {}
    public String getAccessToken() { return accessToken; } public void setAccessToken(String v) { this.accessToken = v; }
    public String getRefreshToken() { return refreshToken; } public void setRefreshToken(String v) { this.refreshToken = v; }
    public String getTokenType() { return tokenType; } public void setTokenType(String v) { this.tokenType = v; }
    public Long getExpiresIn() { return expiresIn; } public void setExpiresIn(Long v) { this.expiresIn = v; }
    public UserResponse getUser() { return user; } public void setUser(UserResponse user) { this.user = user; }
}
