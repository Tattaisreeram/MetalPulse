package com.smarthmalik.metalpulse.dto.response;

import java.math.BigDecimal;

public record PriceAnalyticsDto(
        String metal,
        String metalName,
        String currency,
        String weightUnit,
        String fromDate,
        String toDate,
        BigDecimal startPrice,
        BigDecimal endPrice,
        BigDecimal absoluteChange,
        BigDecimal percentageChange,
        String trend    // UP, DOWN, STABLE
) {}
