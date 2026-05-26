package com.smarthmalik.metalpulse.core.constant;

public final class SQLQueryConstants {

    private SQLQueryConstants() {}

    // Trade queries
    public static final String TRADE_FIND_BY_USER_ID =
            "SELECT t FROM Trade t WHERE t.user.id = :userId ORDER BY t.createdAt DESC";

    public static final String TRADE_FIND_BY_USER_AND_TYPE =
            "SELECT t FROM Trade t WHERE t.user.id = :userId AND t.tradeType = :tradeType ORDER BY t.createdAt DESC";

    public static final String TRADE_FIND_BY_USER_AND_METAL =
            "SELECT t FROM Trade t WHERE t.user.id = :userId AND t.metal = :metal ORDER BY t.createdAt DESC";
}
