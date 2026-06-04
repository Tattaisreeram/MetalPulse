package com.smarthmalik.metalpulse.core.controller;

import com.smarthmalik.metalpulse.dto.request.LoginRequest;
import com.smarthmalik.metalpulse.dto.request.RegisterRequest;
import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.AuthResponse;
import com.smarthmalik.metalpulse.core.constant.URLConstants;
import com.smarthmalik.metalpulse.core.facade.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Register and log in to get a JWT token")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @Operation(summary = "Register a new user")
    @PostMapping(URLConstants.AUTH_REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authFacade.register(request);
    }

    @Operation(summary = "Log in and receive a JWT bearer token")
    @PostMapping(URLConstants.AUTH_LOGIN)
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authFacade.login(request);
    }

    @Operation(summary = "Log out and invalidate the current JWT token")
    @PostMapping(URLConstants.AUTH_LOGOUT)
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return authFacade.logout(token);
    }
}
