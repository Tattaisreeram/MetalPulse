package com.smarthmalik.metalpulse.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradeExecutedEvent(
        Long tradeId,
        Long userId,
        String username,
        String tradeType,
        String metal,
        String currency,
        String weightUnit,
        BigDecimal quantity,
        BigDecimal totalAmount,
        LocalDateTime occurredAt
) {}
