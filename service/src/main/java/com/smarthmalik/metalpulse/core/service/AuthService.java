package com.smarthmalik.metalpulse.core.service;

import com.smarthmalik.metalpulse.dto.request.LoginRequest;
import com.smarthmalik.metalpulse.dto.request.RegisterRequest;
import com.smarthmalik.metalpulse.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
