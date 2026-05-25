package com.smarthmalik.metalpulse.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeDto(
        Long id,
        String tradeType,
        String metal,
        String metalName,
        String currency,
        String weightUnit,
        BigDecimal quantity,
        BigDecimal pricePerUnit,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt
) {}
