package com.smarthmalik.metalpulse.dto.response;

import java.math.BigDecimal;

public record ReturnsDto(
        String metal,
        String metalName,
        String currency,
        String weightUnit,
        int durationDays,
        BigDecimal investmentAmount,
        BigDecimal priceAtInvestment,
        BigDecimal currentPrice,
        BigDecimal quantityPurchased,
        BigDecimal currentValue,
        BigDecimal profitLoss,
        BigDecimal returnPercentage
) {}
