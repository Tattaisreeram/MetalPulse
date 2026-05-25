package com.smarthmalik.metalpulse.core.controller;

import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.PriceAnalyticsDto;
import com.smarthmalik.metalpulse.dto.response.ReturnsDto;
import com.smarthmalik.metalpulse.core.constant.URLConstants;
import com.smarthmalik.metalpulse.core.facade.AnalyticsFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Tag(name = "Analytics", description = "Price change analysis and investment returns calculator")
@SecurityRequirement(name = "bearerAuth")
@Validated
@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsFacade analyticsFacade;

    @Operation(summary = "Analyse price change between two dates")
    @GetMapping(URLConstants.ANALYTICS_PRICE_CHANGE)
    public ApiResponse<PriceAnalyticsDto> getPriceChange(
            @RequestParam(defaultValue = "XAU") String metal,
            @RequestParam(defaultValue = "PKR") String currency,
            @RequestParam(name = "weight_unit", defaultValue = "g") String weightUnit,

            @Parameter(description = "Start date (yyyy-MM-dd)", example = "2024-01-01")
            @RequestParam String fromDate,

            @Parameter(description = "End date (yyyy-MM-dd)", example = "2024-12-31")
            @RequestParam String toDate) {

        return analyticsFacade.getPriceAnalytics(metal, currency, weightUnit, fromDate, toDate);
    }

    @Operation(summary = "Calculate investment returns for a given duration and amount")
    @GetMapping(URLConstants.ANALYTICS_RETURNS)
    public ApiResponse<ReturnsDto> calculateReturns(
            @RequestParam(defaultValue = "XAU") String metal,
            @RequestParam(defaultValue = "PKR") String currency,
            @RequestParam(name = "weight_unit", defaultValue = "g") String weightUnit,

            @Parameter(description = "How many days ago the investment was made", example = "365")
            @Min(1) @RequestParam(defaultValue = "365") int durationDays,

            @Parameter(description = "Initial investment amount", example = "100000")
            @DecimalMin("0.01") @RequestParam BigDecimal investmentAmount) {

        return analyticsFacade.calculateReturns(metal, currency, weightUnit, durationDays, investmentAmount);
    }
}
