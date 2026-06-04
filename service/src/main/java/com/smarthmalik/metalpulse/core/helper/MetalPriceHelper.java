package com.smarthmalik.metalpulse.core.helper;

import com.smarthmalik.metalpulse.exception.ExternalApiException;
import com.smarthmalik.metalpulse.core.constant.ResourceConstants;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalEntry;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalResponse;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
public class MetalPriceHelper {

    // Goldbroker has no tola endpoint — convert from grams (1 tola = 11.6638 g)
    private static final BigDecimal TOLA_PER_GRAM = new BigDecimal("11.6638");
    private static final String TOLA = "tola";

    private final RestClient goldbrokerRestClient;

    public MetalPriceHelper(
            @Qualifier(ResourceConstants.GOLDBROKER_REST_CLIENT) RestClient goldbrokerRestClient) {
        this.goldbrokerRestClient = goldbrokerRestClient;
    }

    @Retry(name = "goldbrokerApi")
    @CircuitBreaker(name = "goldbrokerApi", fallbackMethod = "fetchSpotPriceFallback")
    public GoldbrokerSpotPriceResponse fetchSpotPrice(String metal, String currency, String weightUnit) {
        boolean isTola = TOLA.equalsIgnoreCase(weightUnit);
        String apiUnit = isTola ? "g" : weightUnit;
        try {
            GoldbrokerSpotPriceResponse response = goldbrokerRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(ResourceConstants.GOLDBROKER_SPOT_PRICE)
                            .queryParam("metal", metal)
                            .queryParam("currency", currency)
                            .queryParam("weight_unit", apiUnit)
                            .build())
                    .retrieve()
                    .body(GoldbrokerSpotPriceResponse.class);
            return isTola ? tolaSpot(response) : response;
        } catch (Exception ex) {
            log.error("Failed to fetch spot price for metal={} currency={}", metal, currency, ex);
            throw new ExternalApiException("Unable to retrieve live price from Goldbroker", ex);
        }
    }

    @Retry(name = "goldbrokerApi")
    @CircuitBreaker(name = "goldbrokerApi", fallbackMethod = "fetchHistoricalPricesFallback")
    public GoldbrokerHistoricalResponse fetchHistoricalPrices(String metal, String currency, String weightUnit) {
        boolean isTola = TOLA.equalsIgnoreCase(weightUnit);
        String apiUnit = isTola ? "g" : weightUnit;
        try {
            GoldbrokerHistoricalResponse response = goldbrokerRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(ResourceConstants.GOLDBROKER_HISTORICAL_PRICES)
                            .queryParam("metal", metal)
                            .queryParam("currency", currency)
                            .queryParam("weight_unit", apiUnit)
                            .build())
                    .retrieve()
                    .body(GoldbrokerHistoricalResponse.class);
            return isTola ? tolaHistorical(response) : response;
        } catch (Exception ex) {
            log.error("Failed to fetch historical prices for metal={} currency={}", metal, currency, ex);
            throw new ExternalApiException("Unable to retrieve historical prices from Goldbroker", ex);
        }
    }

    @Retry(name = "goldbrokerApi")
    @CircuitBreaker(name = "goldbrokerApi", fallbackMethod = "fetchFullHistoryFallback")
    public GoldbrokerHistoricalResponse fetchFullHistory(String metal, String currency, String weightUnit) {
        boolean isTola = TOLA.equalsIgnoreCase(weightUnit);
        String apiUnit = isTola ? "g" : weightUnit;
        try {
            GoldbrokerHistoricalResponse response = goldbrokerRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(ResourceConstants.GOLDBROKER_FULL_HISTORY)
                            .queryParam("metal", metal)
                            .queryParam("currency", currency)
                            .queryParam("weight_unit", apiUnit)
                            .build())
                    .retrieve()
                    .body(GoldbrokerHistoricalResponse.class);
            return isTola ? tolaHistorical(response) : response;
        } catch (Exception ex) {
            log.error("Failed to fetch full history for metal={} currency={}", metal, currency, ex);
            throw new ExternalApiException("Unable to retrieve full price history from Goldbroker", ex);
        }
    }

    // --- Circuit breaker fallbacks ---

    private GoldbrokerSpotPriceResponse fetchSpotPriceFallback(
            String metal, String currency, String weightUnit, Throwable ex) {
        log.warn("Circuit breaker OPEN — spot price unavailable: metal={} currency={}: {}", metal, currency, ex.getMessage());
        throw new ExternalApiException("Metal price service is temporarily unavailable. Please try again shortly.", ex);
    }

    private GoldbrokerHistoricalResponse fetchHistoricalPricesFallback(
            String metal, String currency, String weightUnit, Throwable ex) {
        log.warn("Circuit breaker OPEN — historical prices unavailable: metal={} currency={}: {}", metal, currency, ex.getMessage());
        throw new ExternalApiException("Historical price data is temporarily unavailable. Please try again shortly.", ex);
    }

    private GoldbrokerHistoricalResponse fetchFullHistoryFallback(
            String metal, String currency, String weightUnit, Throwable ex) {
        log.warn("Circuit breaker OPEN — full history unavailable: metal={} currency={}: {}", metal, currency, ex.getMessage());
        throw new ExternalApiException("Full price history is temporarily unavailable. Please try again shortly.", ex);
    }

    private GoldbrokerSpotPriceResponse tolaSpot(GoldbrokerSpotPriceResponse r) {
        if (r == null) return null;
        return new GoldbrokerSpotPriceResponse(
                TOLA,
                scale(r.price()),
                scale(r.bid()),
                scale(r.ask()),
                scale(r.change())
        );
    }

    private GoldbrokerHistoricalResponse tolaHistorical(GoldbrokerHistoricalResponse r) {
        if (r == null) return null;
        List<GoldbrokerHistoricalEntry> converted = r.data().stream()
                .map(e -> new GoldbrokerHistoricalEntry(e.date(), scale(e.price())))
                .toList();
        return new GoldbrokerHistoricalResponse(
                new GoldbrokerHistoricalResponse.Embedded(converted)
        );
    }

    private BigDecimal scale(BigDecimal value) {
        if (value == null) return null;
        return value.multiply(TOLA_PER_GRAM).setScale(4, RoundingMode.HALF_UP);
    }
}
