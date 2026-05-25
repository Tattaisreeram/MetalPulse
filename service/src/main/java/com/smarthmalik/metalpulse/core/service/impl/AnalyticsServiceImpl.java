package com.smarthmalik.metalpulse.core.service.impl;

import com.smarthmalik.metalpulse.dto.response.PriceAnalyticsDto;
import com.smarthmalik.metalpulse.dto.response.ReturnsDto;
import com.smarthmalik.metalpulse.exception.ExternalApiException;
import com.smarthmalik.metalpulse.core.helper.AnalyticsHelper;
import com.smarthmalik.metalpulse.core.helper.MetalPriceHelper;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalEntry;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalResponse;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import com.smarthmalik.metalpulse.core.mapper.EntityDTOMapper;
import com.smarthmalik.metalpulse.core.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final MetalPriceHelper metalPriceHelper;
    private final EntityDTOMapper mapper;

    @Override
    public PriceAnalyticsDto getPriceAnalytics(
            String metal, String currency, String weightUnit, String fromDate, String toDate) {

        GoldbrokerHistoricalResponse historical =
                metalPriceHelper.fetchFullHistory(metal, currency, weightUnit);

        List<GoldbrokerHistoricalEntry> entries = historical.data();
        if (entries == null || entries.isEmpty()) {
            throw new ExternalApiException("No historical data available for " + metal);
        }

        BigDecimal startPrice = AnalyticsHelper.findPriceClosestToDate(entries, fromDate)
                .orElseThrow(() -> new ExternalApiException("No price data found for fromDate: " + fromDate));

        BigDecimal endPrice = AnalyticsHelper.findPriceClosestToDate(entries, toDate)
                .orElseThrow(() -> new ExternalApiException("No price data found for toDate: " + toDate));

        BigDecimal absoluteChange   = AnalyticsHelper.calculateAbsoluteChange(startPrice, endPrice);
        BigDecimal percentageChange = AnalyticsHelper.calculatePercentageChange(startPrice, endPrice);
        String trend                = AnalyticsHelper.determineTrend(percentageChange);

        return mapper.toPriceAnalyticsDto(
                metal, currency, weightUnit, fromDate, toDate,
                startPrice, endPrice, absoluteChange, percentageChange, trend
        );
    }

    @Override
    public ReturnsDto calculateReturns(
            String metal, String currency, String weightUnit,
            int durationDays, BigDecimal investmentAmount) {

        // Current spot price
        GoldbrokerSpotPriceResponse spotPrice =
                metalPriceHelper.fetchSpotPrice(metal, currency, weightUnit);
        BigDecimal currentPrice = spotPrice.price();

        // Historical price from durationDays ago
        GoldbrokerHistoricalResponse historical =
                metalPriceHelper.fetchFullHistory(metal, currency, weightUnit);

        String targetDate = LocalDate.now()
                .minusDays(durationDays)
                .format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<GoldbrokerHistoricalEntry> entries = historical.data();
        BigDecimal priceAtInvestment = (entries != null)
                ? AnalyticsHelper.findPriceClosestToDate(entries, targetDate)
                        .orElse(currentPrice)
                : currentPrice;

        BigDecimal quantityPurchased = AnalyticsHelper.quantityFromInvestment(investmentAmount, priceAtInvestment);
        BigDecimal currentValue      = AnalyticsHelper.calculateCurrentValue(quantityPurchased, currentPrice);
        BigDecimal profitLoss        = AnalyticsHelper.calculateAbsoluteChange(investmentAmount, currentValue);
        BigDecimal returnPercentage  = AnalyticsHelper.calculatePercentageChange(investmentAmount, currentValue);

        return mapper.toReturnsDto(
                metal, currency, weightUnit, durationDays,
                investmentAmount, priceAtInvestment, currentPrice,
                quantityPurchased, currentValue, profitLoss, returnPercentage
        );
    }
}
