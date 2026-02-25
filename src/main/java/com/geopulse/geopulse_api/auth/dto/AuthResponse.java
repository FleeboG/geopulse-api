package com.geopulse.geopulse_api.auth.dto;

public record AuthResponse(
    String accessToken,
    String tokenType
) {
    public static AuthResponse bearer(String accessToken) {
        return new AuthResponse(accessToken, "Bearer");
    }
}