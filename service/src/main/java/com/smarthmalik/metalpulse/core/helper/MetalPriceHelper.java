package com.smarthmalik.metalpulse.core.helper;

import com.smarthmalik.metalpulse.exception.ExternalApiException;
import com.smarthmalik.metalpulse.core.constant.ResourceConstants;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalResponse;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class MetalPriceHelper {

    private final RestClient goldbrokerRestClient;

    public MetalPriceHelper(
            @Qualifier(ResourceConstants.GOLDBROKER_REST_CLIENT) RestClient goldbrokerRestClient) {
        this.goldbrokerRestClient = goldbrokerRestClient;
    }

    public GoldbrokerSpotPriceResponse fetchSpotPrice(String metal, String currency, String weightUnit) {
        try {
            return goldbrokerRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(ResourceConstants.GOLDBROKER_SPOT_PRICE)
                            .queryParam("metal", metal)
                            .queryParam("currency", currency)
                            .queryParam("weight_unit", weightUnit)
                            .build())
                    .retrieve()
                    .body(GoldbrokerSpotPriceResponse.class);
        } catch (Exception ex) {
            log.error("Failed to fetch spot price for metal={} currency={}", metal, currency, ex);
            throw new ExternalApiException("Unable to retrieve live price from Goldbroker", ex);
        }
    }

    public GoldbrokerHistoricalResponse fetchHistoricalPrices(String metal, String currency, String weightUnit) {
        try {
            return goldbrokerRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(ResourceConstants.GOLDBROKER_HISTORICAL_PRICES)
                            .queryParam("metal", metal)
                            .queryParam("currency", currency)
                            .queryParam("weight_unit", weightUnit)
                            .build())
                    .retrieve()
                    .body(GoldbrokerHistoricalResponse.class);
        } catch (Exception ex) {
            log.error("Failed to fetch historical prices for metal={} currency={}", metal, currency, ex);
            throw new ExternalApiException("Unable to retrieve historical prices from Goldbroker", ex);
        }
    }

    public GoldbrokerHistoricalResponse fetchFullHistory(String metal, String currency, String weightUnit) {
        try {
            return goldbrokerRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(ResourceConstants.GOLDBROKER_FULL_HISTORY)
                            .queryParam("metal", metal)
                            .queryParam("currency", currency)
                            .queryParam("weight_unit", weightUnit)
                            .build())
                    .retrieve()
                    .body(GoldbrokerHistoricalResponse.class);
        } catch (Exception ex) {
            log.error("Failed to fetch full history for metal={} currency={}", metal, currency, ex);
            throw new ExternalApiException("Unable to retrieve full price history from Goldbroker", ex);
        }
    }
}
