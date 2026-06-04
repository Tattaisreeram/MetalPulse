package com.smarthmalik.metalpulse;

import com.smarthmalik.metalpulse.core.helper.MetalPriceHelper;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import com.smarthmalik.metalpulse.core.service.MetalPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Verifies the cache-aside pattern: calling getSpotPrice() twice with identical params
 * should result in only one call to the external Goldbroker API.
 */
@SpringBootTest
@Testcontainers
class SpotPriceCacheIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @MockitoBean
    MetalPriceHelper metalPriceHelper;

    @Autowired
    MetalPriceService metalPriceService;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // Clear cache so each test starts from a cache-miss state
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });

        when(metalPriceHelper.fetchSpotPrice(anyString(), anyString(), anyString()))
                .thenReturn(new GoldbrokerSpotPriceResponse(
                        "oz",
                        new BigDecimal("2300.00"),
                        new BigDecimal("2299.00"),
                        new BigDecimal("2301.00"),
                        new BigDecimal("15.00")));
    }

    @Test
    void getSpotPrice_secondCallIsServedFromCache_helperCalledOnce() {
        metalPriceService.getSpotPrice("XAU", "USD", "oz");
        metalPriceService.getSpotPrice("XAU", "USD", "oz");

        // Cache hit on second call — Goldbroker API should only have been contacted once
        verify(metalPriceHelper, times(1)).fetchSpotPrice("XAU", "USD", "oz");
    }

    @Test
    void getSpotPrice_differentParams_eachParamCombinationMissesCache() {
        metalPriceService.getSpotPrice("XAU", "USD", "oz");
        metalPriceService.getSpotPrice("XAG", "USD", "oz"); // different metal → different cache key

        verify(metalPriceHelper, times(1)).fetchSpotPrice("XAU", "USD", "oz");
        verify(metalPriceHelper, times(1)).fetchSpotPrice("XAG", "USD", "oz");
    }
}
