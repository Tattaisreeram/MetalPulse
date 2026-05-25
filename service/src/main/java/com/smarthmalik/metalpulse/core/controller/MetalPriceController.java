package com.smarthmalik.metalpulse.core.controller;

import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.HistoricalPriceDto;
import com.smarthmalik.metalpulse.dto.response.HistoricalPricePageDto;
import com.smarthmalik.metalpulse.dto.response.SpotPriceDto;
import com.smarthmalik.metalpulse.core.constant.URLConstants;
import com.smarthmalik.metalpulse.core.facade.MetalPriceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Metal Prices", description = "Live and historical precious metal price feeds from Goldbroker")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class MetalPriceController {

    private final MetalPriceFacade metalPriceFacade;

    @Operation(summary = "Get live spot price for a metal")
    @GetMapping(URLConstants.METAL_SPOT_PRICE)
    public ApiResponse<SpotPriceDto> getSpotPrice(
            @Parameter(description = "Metal symbol", example = "XAU")
            @RequestParam(defaultValue = "XAU") String metal,

            @Parameter(description = "Currency code", example = "PKR")
            @RequestParam(defaultValue = "PKR") String currency,

            @Parameter(description = "Weight unit", example = "g")
            @RequestParam(name = "weight_unit", defaultValue = "g") String weightUnit) {

        return metalPriceFacade.getSpotPrice(metal, currency, weightUnit);
    }

    @Operation(summary = "Get paginated historical prices")
    @GetMapping(URLConstants.METAL_HISTORICAL)
    public ApiResponse<HistoricalPricePageDto> getHistoricalPrices(
            @RequestParam(defaultValue = "XAU") String metal,
            @RequestParam(defaultValue = "PKR") String currency,
            @RequestParam(name = "weight_unit", defaultValue = "g") String weightUnit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        return metalPriceFacade.getHistoricalPrices(metal, currency, weightUnit, page, size);
    }

    @Operation(summary = "Get full price history (all available records)")
    @GetMapping(URLConstants.METAL_FULL_HISTORY)
    public ApiResponse<List<HistoricalPriceDto>> getFullHistory(
            @RequestParam(defaultValue = "XAU") String metal,
            @RequestParam(defaultValue = "PKR") String currency,
            @RequestParam(name = "weight_unit", defaultValue = "g") String weightUnit) {

        return metalPriceFacade.getFullHistory(metal, currency, weightUnit);
    }
}
