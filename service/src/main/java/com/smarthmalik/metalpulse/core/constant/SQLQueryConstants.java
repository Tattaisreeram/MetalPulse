package com.smarthmalik.metalpulse.core.constant;

public final class SQLQueryConstants {

    private SQLQueryConstants() {}

    // User queries
    public static final String USER_FIND_BY_USERNAME =
            "SELECT u FROM User u WHERE u.username = :username";

    public static final String USER_FIND_BY_EMAIL =
            "SELECT u FROM User u WHERE u.email = :email";

    public static final String USER_EXISTS_BY_USERNAME =
            "SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username";

    public static final String USER_EXISTS_BY_EMAIL =
            "SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email";

    // Trade queries
    public static final String TRADE_FIND_BY_USER_ID =
            "SELECT t FROM Trade t WHERE t.user.id = :userId ORDER BY t.createdAt DESC";

    public static final String TRADE_FIND_BY_USER_AND_TYPE =
            "SELECT t FROM Trade t WHERE t.user.id = :userId AND t.tradeType = :tradeType ORDER BY t.createdAt DESC";

    public static final String TRADE_FIND_BY_USER_AND_METAL =
            "SELECT t FROM Trade t WHERE t.user.id = :userId AND t.metal = :metal ORDER BY t.createdAt DESC";
}
