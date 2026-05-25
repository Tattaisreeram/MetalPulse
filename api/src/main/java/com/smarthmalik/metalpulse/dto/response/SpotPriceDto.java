package com.smarthmalik.metalpulse.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SpotPriceDto(
        String metal,
        String metalName,
        String currency,
        String weightUnit,
        BigDecimal price,
        BigDecimal bid,
        BigDecimal ask,
        BigDecimal change,
        BigDecimal changePercent,
        LocalDateTime fetchedAt
) {}
