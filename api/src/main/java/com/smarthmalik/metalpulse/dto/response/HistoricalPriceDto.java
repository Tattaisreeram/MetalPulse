package com.smarthmalik.metalpulse.dto.response;

import java.math.BigDecimal;

public record HistoricalPriceDto(
        String date,
        BigDecimal price
) {}
