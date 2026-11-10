package com.example.bankalite.user;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String username
) {
    public static AuthResponse of(String token, String username) {
        return new AuthResponse(token, "Bearer", username);
    }
}