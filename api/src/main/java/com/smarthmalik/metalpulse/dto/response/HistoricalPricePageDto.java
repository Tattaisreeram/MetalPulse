package com.smarthmalik.metalpulse.dto.response;

import java.util.List;

public record HistoricalPricePageDto(
        String metal,
        String currency,
        String weightUnit,
        List<HistoricalPriceDto> prices,
        int page,
        int size,
        int totalPages,
        long totalRecords
) {}
