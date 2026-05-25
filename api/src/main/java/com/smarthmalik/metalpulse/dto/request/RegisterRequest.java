package com.smarthmalik.metalpulse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "New user registration details")
public record RegisterRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be 3–50 characters")
        @Schema(example = "john_doe")
        String username,

        @Email(message = "Must be a valid email address")
        @NotBlank(message = "Email is required")
        @Schema(example = "john@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Schema(example = "secret123")
        String password
) {}
