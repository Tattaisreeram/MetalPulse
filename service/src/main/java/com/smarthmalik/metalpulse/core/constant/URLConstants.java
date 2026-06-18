package com.smarthmalik.metalpulse.core.constant;

public final class URLConstants {

    private URLConstants() {}

    private static final String API_V1 = "/api/v1";

    // Auth
    public static final String AUTH_BASE     = API_V1 + "/auth";
    public static final String AUTH_REGISTER = AUTH_BASE + "/register";
    public static final String AUTH_LOGIN    = AUTH_BASE + "/login";
    public static final String AUTH_LOGOUT   = AUTH_BASE + "/logout";

    // Metal prices
    public static final String METAL_BASE         = API_V1 + "/metals";
    public static final String METAL_SPOT_PRICE   = METAL_BASE + "/spot-price";
    public static final String METAL_HISTORICAL   = METAL_BASE + "/historical";
    public static final String METAL_FULL_HISTORY = METAL_BASE + "/full-history";

    // Trades
    public static final String TRADE_BASE     = API_V1 + "/trades";
    public static final String TRADE_BUY      = TRADE_BASE + "/buy";
    public static final String TRADE_SELL     = TRADE_BASE + "/sell";
    public static final String TRADE_HOLD     = TRADE_BASE + "/hold";
    public static final String TRADE_DEPOSIT  = TRADE_BASE + "/deposit";
    public static final String TRADE_WITHDRAW = TRADE_BASE + "/withdraw";
    public static final String TRADE_BALANCE  = TRADE_BASE + "/balance";
    public static final String TRADE_HISTORY  = TRADE_BASE + "/history";

    // Analytics
    public static final String ANALYTICS_BASE         = API_V1 + "/analytics";
    public static final String ANALYTICS_PRICE_CHANGE = ANALYTICS_BASE + "/price-change";
    public static final String ANALYTICS_RETURNS      = ANALYTICS_BASE + "/returns";

    // AI Assistant
    public static final String ASSISTANT_BASE = API_V1 + "/assistant";
    public static final String ASSISTANT_ASK  = ASSISTANT_BASE + "/ask";

    // Public endpoints (no auth required)
    public static final String[] PUBLIC_ENDPOINTS = {
            AUTH_REGISTER, AUTH_LOGIN,
            "/swagger-ui/**", "/api-docs/**", "/swagger-ui.html"
    };
}
