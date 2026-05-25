package com.smarthmalik.metalpulse.core.facade;

import com.smarthmalik.metalpulse.dto.response.ApiResponse;
import com.smarthmalik.metalpulse.dto.response.HistoricalPriceDto;
import com.smarthmalik.metalpulse.dto.response.HistoricalPricePageDto;
import com.smarthmalik.metalpulse.dto.response.SpotPriceDto;
import com.smarthmalik.metalpulse.core.service.MetalPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetalPriceFacade {

    private final MetalPriceService metalPriceService;

    public ApiResponse<SpotPriceDto> getSpotPrice(String metal, String currency, String weightUnit) {
        SpotPriceDto dto = metalPriceService.getSpotPrice(metal, currency, weightUnit);
        return ApiResponse.success(dto);
    }

    public ApiResponse<HistoricalPricePageDto> getHistoricalPrices(
            String metal, String currency, String weightUnit, int page, int size) {

        HistoricalPricePageDto dto = metalPriceService.getHistoricalPrices(metal, currency, weightUnit, page, size);
        return ApiResponse.success(dto);
    }

    public ApiResponse<List<HistoricalPriceDto>> getFullHistory(
            String metal, String currency, String weightUnit) {

        List<HistoricalPriceDto> history = metalPriceService.getFullHistory(metal, currency, weightUnit);
        return ApiResponse.success(history);
    }
}
