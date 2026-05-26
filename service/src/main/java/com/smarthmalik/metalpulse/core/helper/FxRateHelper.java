package com.smarthmalik.metalpulse.core.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smarthmalik.metalpulse.SupportedCurrency;
import com.smarthmalik.metalpulse.exception.UnsupportedCurrencyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
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
        SupportedCurrency supportedCurrency = normalizeCurrency(currency);

        if (supportedCurrency == SupportedCurrency.USD) {
            return amount;
        }

        BigDecimal rate = getRates().get(supportedCurrency.name());
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnsupportedCurrencyException(supportedCurrency.name(), "rate unavailable");
        }

        return amount.divide(rate, 6, RoundingMode.HALF_UP);
    }

    private SupportedCurrency normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new UnsupportedCurrencyException("blank", "missing currency");
        }

        String normalized = currency.trim().toUpperCase(Locale.ROOT);
        try {
            return SupportedCurrency.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedCurrencyException(normalized);
        }
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
