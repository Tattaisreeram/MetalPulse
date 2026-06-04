package com.smarthmalik.metalpulse;

import com.smarthmalik.metalpulse.core.entity.Trade;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.helper.MetalPriceHelper;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import com.smarthmalik.metalpulse.core.repository.TradeRepository;
import com.smarthmalik.metalpulse.core.repository.UserRepository;
import com.smarthmalik.metalpulse.core.service.TradeService;
import com.smarthmalik.metalpulse.dto.request.TradeRequest;
import com.smarthmalik.metalpulse.dto.response.TradeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Verifies the end-to-end trade flow against a real MySQL container:
 * balance is deducted and the trade record is persisted correctly.
 */
@SpringBootTest
@Testcontainers
class TradeFlowIT {

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
    TradeService tradeService;

    @Autowired
    TradeRepository tradeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        tradeRepository.deleteAll();
        userRepository.deleteAll();

        when(metalPriceHelper.fetchSpotPrice(anyString(), anyString(), anyString()))
                .thenReturn(new GoldbrokerSpotPriceResponse(
                        "oz",
                        new BigDecimal("2300.00"),
                        new BigDecimal("2299.00"),
                        new BigDecimal("2301.00"),
                        new BigDecimal("15.00")));
    }

    @Test
    void buy_persistsTradeAndDeductsBalanceInUsd() {
        User user = userRepository.save(User.builder()
                .username("testbuyer")
                .email("testbuyer@test.com")
                .password(passwordEncoder.encode("password"))
                .balance(new BigDecimal("10000.00"))
                .build());

        TradeRequest request = new TradeRequest("XAU", "USD", "oz", new BigDecimal("1.0"));
        TradeDto result = tradeService.buy(user, request);

        assertThat(result.tradeType()).isEqualTo("BUY");
        assertThat(result.metal()).isEqualTo("XAU");
        assertThat(result.totalAmount()).isEqualByComparingTo("2300.00");
        assertThat(result.status()).isEqualTo("COMPLETED");

        List<Trade> trades = tradeRepository.findAll();
        assertThat(trades).hasSize(1);
        assertThat(trades.get(0).getTradeType()).isEqualTo(Trade.TradeType.BUY);

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo("7700.00"); // 10000 - 2300
    }

    @Test
    void sell_persistsTradeAndCreditsBalance() {
        User user = userRepository.save(User.builder()
                .username("testseller")
                .email("testseller@test.com")
                .password(passwordEncoder.encode("password"))
                .balance(new BigDecimal("500.00"))
                .build());

        TradeRequest request = new TradeRequest("XAU", "USD", "oz", new BigDecimal("0.5"));
        TradeDto result = tradeService.sell(user, request);

        assertThat(result.tradeType()).isEqualTo("SELL");
        assertThat(result.totalAmount()).isEqualByComparingTo("1150.00"); // 2300 * 0.5

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo("1650.00"); // 500 + 1150
    }
}
