package com.smarthmalik.metalpulse.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String username,
        String email,
        String role
) {}
