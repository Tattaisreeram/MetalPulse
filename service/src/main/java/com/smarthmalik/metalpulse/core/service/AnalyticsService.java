package com.smarthmalik.metalpulse.core.service;

import com.smarthmalik.metalpulse.dto.response.PriceAnalyticsDto;
import com.smarthmalik.metalpulse.dto.response.ReturnsDto;

import java.math.BigDecimal;

public interface AnalyticsService {

    PriceAnalyticsDto getPriceAnalytics(String metal, String currency, String weightUnit,
                                         String fromDate, String toDate);

    ReturnsDto calculateReturns(String metal, String currency, String weightUnit,
                                int durationDays, BigDecimal investmentAmount);
}
