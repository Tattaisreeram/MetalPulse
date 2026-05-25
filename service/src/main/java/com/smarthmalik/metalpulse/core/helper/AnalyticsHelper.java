package com.smarthmalik.metalpulse.core.helper;

import com.smarthmalik.metalpulse.core.constant.ResourceConstants;
import com.smarthmalik.metalpulse.core.helper.api.GoldbrokerHistoricalEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class AnalyticsHelper {

    private static final BigDecimal STABLE_THRESHOLD = new BigDecimal("0.01");
    private static final int SCALE = 4;

    private AnalyticsHelper() {}

    public static BigDecimal calculatePercentageChange(BigDecimal startPrice, BigDecimal endPrice) {
        if (startPrice == null || startPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return endPrice.subtract(startPrice)
                .divide(startPrice, SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public static BigDecimal calculateAbsoluteChange(BigDecimal startPrice, BigDecimal endPrice) {
        return endPrice.subtract(startPrice).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static String determineTrend(BigDecimal percentageChange) {
        if (percentageChange.abs().compareTo(STABLE_THRESHOLD) <= 0) {
            return ResourceConstants.TREND_STABLE;
        }
        return percentageChange.compareTo(BigDecimal.ZERO) > 0
                ? ResourceConstants.TREND_UP
                : ResourceConstants.TREND_DOWN;
    }

    public static Optional<BigDecimal> findPriceClosestToDate(
            List<GoldbrokerHistoricalEntry> entries, String targetDate) {

        LocalDate target = LocalDate.parse(targetDate);
        return entries.stream()
                .filter(e -> e.date() != null && e.price() != null)
                .min(Comparator.comparingLong(e ->
                        Math.abs(ChronoUnit.DAYS.between(LocalDate.parse(e.date()), target))))
                .map(GoldbrokerHistoricalEntry::price);
    }

    public static BigDecimal calculateCurrentValue(BigDecimal quantity, BigDecimal currentPrice) {
        return quantity.multiply(currentPrice).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal quantityFromInvestment(BigDecimal investmentAmount, BigDecimal pricePerUnit) {
        if (pricePerUnit == null || pricePerUnit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return investmentAmount.divide(pricePerUnit, 6, RoundingMode.HALF_UP);
    }
}
