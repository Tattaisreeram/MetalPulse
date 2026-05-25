package com.smarthmalik.metalpulse.core.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class FxRateHelper {

    private static final String FX_API_URL = "https://open.er-api.com/v6/latest/USD";
    private static final long CACHE_TTL_MS = 3_600_000L;

    private final RestClient restClient = RestClient.create();
    private volatile Map<String, BigDecimal> cachedRates = new ConcurrentHashMap<>();
    private volatile long cacheTimestamp = 0L;

    public BigDecimal toUsd(BigDecimal amount, String currency) {
        if (currency == null || "USD".equalsIgnoreCase(currency)) return amount;
        BigDecimal rate = getRates().get(currency.toUpperCase());
        if (rate == null || rate.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("No FX rate for currency={}, using raw amount", currency);
            return amount;
        }
        return amount.divide(rate, 6, RoundingMode.HALF_UP);
    }

    private Map<String, BigDecimal> getRates() {
        long now = System.currentTimeMillis();
        if (now - cacheTimestamp < CACHE_TTL_MS && !cachedRates.isEmpty()) {
            return cachedRates;
        }
        try {
            FxRateResponse response = restClient.get()
                    .uri(FX_API_URL)
                    .retrieve()
                    .body(FxRateResponse.class);
            if (response != null && response.rates() != null) {
                cachedRates = new ConcurrentHashMap<>(response.rates());
                cacheTimestamp = now;
                log.info("FX rates refreshed, {} currencies loaded", cachedRates.size());
            }
        } catch (Exception e) {
            log.error("Failed to fetch FX rates", e);
        }
        return cachedRates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FxRateResponse(Map<String, BigDecimal> rates) {}
}
