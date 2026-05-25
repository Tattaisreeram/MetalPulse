package com.smarthmalik.metalpulse.core.service;

import com.smarthmalik.metalpulse.dto.response.HistoricalPriceDto;
import com.smarthmalik.metalpulse.dto.response.HistoricalPricePageDto;
import com.smarthmalik.metalpulse.dto.response.SpotPriceDto;

import java.util.List;

public interface MetalPriceService {

    SpotPriceDto getSpotPrice(String metal, String currency, String weightUnit);

    HistoricalPricePageDto getHistoricalPrices(String metal, String currency, String weightUnit, int page, int size);

    List<HistoricalPriceDto> getFullHistory(String metal, String currency, String weightUnit);
}
