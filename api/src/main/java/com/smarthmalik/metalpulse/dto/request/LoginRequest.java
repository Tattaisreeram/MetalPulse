package com.smarthmalik.metalpulse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login credentials")
public record LoginRequest(

        @NotBlank(message = "Username is required")
        @Schema(example = "john_doe")
        String username,

        @NotBlank(message = "Password is required")
        @Schema(example = "secret123")
        String password
) {}
