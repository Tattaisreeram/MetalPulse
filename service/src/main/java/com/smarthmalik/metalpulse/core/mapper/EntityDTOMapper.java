package com.smarthmalik.metalpulse.core.mapper;

import com.smarthmalik.metalpulse.dto.response.*;
import com.smarthmalik.metalpulse.core.entity.Trade;
import com.smarthmalik.metalpulse.dto.response.UserDto;
import com.smarthmalik.metalpulse.core.entity.User;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalEntry;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerSpotPriceResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class EntityDTOMapper {

    private static final Map<String, String> METAL_NAMES = Map.of(
            "XAU", "Gold",
            "XAG", "Silver",
            "XPT", "Platinum",
            "XPD", "Palladium"
    );

    public String resolveMetalName(String metalCode) {
        if (metalCode == null) return null;
        return METAL_NAMES.getOrDefault(metalCode, "Unknown");
    }

    public SpotPriceDto toSpotPriceDto(GoldbrokerSpotPriceResponse response, String metal, String currency) {
        BigDecimal change = response.change() != null ? response.change() : BigDecimal.ZERO;
        BigDecimal price  = response.price()  != null ? response.price()  : BigDecimal.ZERO;
        BigDecimal prevPrice = price.subtract(change);
        BigDecimal changePercent = prevPrice.compareTo(BigDecimal.ZERO) != 0
                ? change.divide(prevPrice, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        return new SpotPriceDto(
                metal,
                resolveMetalName(metal),
                currency,
                response.weightUnit(),
                price,
                response.bid()  != null ? response.bid()  : BigDecimal.ZERO,
                response.ask()  != null ? response.ask()  : BigDecimal.ZERO,
                change,
                changePercent,
                LocalDateTime.now()
        );
    }

    public HistoricalPriceDto toHistoricalPriceDto(GoldbrokerHistoricalEntry entry) {
        return new HistoricalPriceDto(entry.date(), entry.price());
    }

    public TradeDto toTradeDto(Trade trade) {
        return new TradeDto(
                trade.getId(),
                trade.getTradeType().name(),
                trade.getMetal(),
                resolveMetalName(trade.getMetal()),
                trade.getCurrency(),
                trade.getWeightUnit(),
                trade.getQuantity(),
                trade.getPricePerUnit(),
                trade.getTotalAmount(),
                trade.getStatus().name(),
                trade.getCreatedAt()
        );
    }

    public BalanceDto toBalanceDto(User user) {
        return new BalanceDto(user.getId(), user.getUsername(), user.getBalance());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole().name(), user.getBalance());
    }

    public AuthResponse toAuthResponse(UserDto userDto, String token) {
        return new AuthResponse(token, "Bearer", userDto.userId(), userDto.username(),
                userDto.email(), userDto.role());
    }

    public PriceAnalyticsDto toPriceAnalyticsDto(
            String metal, String currency, String weightUnit,
            String fromDate, String toDate,
            BigDecimal startPrice, BigDecimal endPrice,
            BigDecimal absoluteChange, BigDecimal percentageChange, String trend) {

        return new PriceAnalyticsDto(
                metal, resolveMetalName(metal), currency, weightUnit,
                fromDate, toDate, startPrice, endPrice,
                absoluteChange, percentageChange, trend
        );
    }

    public ReturnsDto toReturnsDto(
            String metal, String currency, String weightUnit, int durationDays,
            BigDecimal investmentAmount, BigDecimal priceAtInvestment, BigDecimal currentPrice,
            BigDecimal quantityPurchased, BigDecimal currentValue,
            BigDecimal profitLoss, BigDecimal returnPercentage) {

        return new ReturnsDto(
                metal, resolveMetalName(metal), currency, weightUnit, durationDays,
                investmentAmount, priceAtInvestment, currentPrice,
                quantityPurchased, currentValue, profitLoss, returnPercentage
        );
    }
}
