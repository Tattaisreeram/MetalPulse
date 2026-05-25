package com.smarthmalik.metalpulse.core.facade;

import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.PriceAnalyticsDto;
import com.smarthmalik.metalpulse.dto.response.ReturnsDto;
import com.smarthmalik.metalpulse.core.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsFacade {

    private final AnalyticsService analyticsService;

    public ApiResponse<PriceAnalyticsDto> getPriceAnalytics(
            String metal, String currency, String weightUnit, String fromDate, String toDate) {

        PriceAnalyticsDto dto = analyticsService.getPriceAnalytics(metal, currency, weightUnit, fromDate, toDate);
        return ApiResponse.success(dto);
    }

    public ApiResponse<ReturnsDto> calculateReturns(
            String metal, String currency, String weightUnit,
            int durationDays, BigDecimal investmentAmount) {

        ReturnsDto dto = analyticsService.calculateReturns(metal, currency, weightUnit, durationDays, investmentAmount);
        return ApiResponse.success(dto);
    }
}
