package com.smarthmalik.metalpulse.core.service;

import com.smarthmalik.metalpulse.dto.request.RegisterRequest;
import com.smarthmalik.metalpulse.dto.response.UserDto;

public interface AuthService {

    UserDto register(RegisterRequest request);
}
