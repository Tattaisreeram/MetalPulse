package com.smarthmalik.metalpulse.core.constant;

public final class ResourceConstants {

    private ResourceConstants() {}

    // Bean names
    public static final String GOLDBROKER_REST_CLIENT = "goldbrokerRestClient";

    // Metal display names (used in place of magic strings throughout the codebase)
    public static final String METAL_GOLD      = "Gold";
    public static final String METAL_SILVER    = "Silver";
    public static final String METAL_PLATINUM  = "Platinum";
    public static final String METAL_PALLADIUM = "Palladium";
    public static final String METAL_UNKNOWN   = "Unknown";

    // Trade type display strings
    public static final String TRADE_BUY      = "BUY";
    public static final String TRADE_SELL     = "SELL";
    public static final String TRADE_HOLD     = "HOLD";
    public static final String TRADE_DEPOSIT  = "DEPOSIT";
    public static final String TRADE_WITHDRAW = "WITHDRAW";

    // Analytics trends
    public static final String TREND_UP     = "UP";
    public static final String TREND_DOWN   = "DOWN";
    public static final String TREND_STABLE = "STABLE";

    // Goldbroker API paths (relative to base URL)
    public static final String GOLDBROKER_SPOT_PRICE       = "/spot-price";
    public static final String GOLDBROKER_HISTORICAL_PRICES = "/spot-prices";
    public static final String GOLDBROKER_FULL_HISTORY      = "/historical-spot-prices";
}
