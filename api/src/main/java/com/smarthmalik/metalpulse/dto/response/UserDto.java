package com.smarthmalik.metalpulse.dto.response;

import java.math.BigDecimal;

public record UserDto(Long userId, String username, String email, String role, BigDecimal balance) {}
