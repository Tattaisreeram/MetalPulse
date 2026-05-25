package com.smarthmalik.metalpulse.dto.response;

import java.math.BigDecimal;

public record BalanceDto(
        Long userId,
        String username,
        BigDecimal balance
) {}
