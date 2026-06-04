package com.smarthmalik.metalpulse.core.service.impl;

import com.smarthmalik.metalpulse.configuration.CacheConfig;
import com.smarthmalik.metalpulse.dto.response.HistoricalPriceDto;
import com.smarthmalik.metalpulse.dto.response.HistoricalPricePageDto;
import com.smarthmalik.metalpulse.dto.response.SpotPriceDto;
import com.smarthmalik.metalpulse.core.helper.MetalPriceHelper;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalResponse;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import com.smarthmalik.metalpulse.core.mapper.EntityDTOMapper;
import com.smarthmalik.metalpulse.core.service.MetalPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetalPriceServiceImpl implements MetalPriceService {

    private final MetalPriceHelper metalPriceHelper;
    private final EntityDTOMapper mapper;

    @Override
    @Cacheable(value = CacheConfig.SPOT_PRICE_CACHE, key = "#metal + ':' + #currency + ':' + #weightUnit")
    public SpotPriceDto getSpotPrice(String metal, String currency, String weightUnit) {
        GoldbrokerSpotPriceResponse response = metalPriceHelper.fetchSpotPrice(metal, currency, weightUnit);
        return mapper.toSpotPriceDto(response, metal, currency);
    }

    @Override
    public HistoricalPricePageDto getHistoricalPrices(
            String metal, String currency, String weightUnit, int page, int size) {

        GoldbrokerHistoricalResponse response =
                metalPriceHelper.fetchHistoricalPrices(metal, currency, weightUnit);

        List<HistoricalPriceDto> allPrices = response.data() == null
                ? Collections.emptyList()
                : response.data().stream().map(mapper::toHistoricalPriceDto).toList();

        int total      = allPrices.size();
        int totalPages = (int) Math.ceil((double) total / size);
        int fromIndex  = Math.min(page * size, total);
        int toIndex    = Math.min(fromIndex + size, total);

        return new HistoricalPricePageDto(
                metal, currency, weightUnit,
                allPrices.subList(fromIndex, toIndex),
                page, size, totalPages, total
        );
    }

    @Override
    @Cacheable(value = CacheConfig.FULL_HISTORY_CACHE, key = "#metal + ':' + #currency + ':' + #weightUnit")
    public List<HistoricalPriceDto> getFullHistory(String metal, String currency, String weightUnit) {
        GoldbrokerHistoricalResponse response = metalPriceHelper.fetchFullHistory(metal, currency, weightUnit);
        if (response.data() == null) return Collections.emptyList();
        return response.data().stream().map(mapper::toHistoricalPriceDto).toList();
    }
}
