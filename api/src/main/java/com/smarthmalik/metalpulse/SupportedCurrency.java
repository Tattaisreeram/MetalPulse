package com.smarthmalik.metalpulse;

public enum SupportedCurrency {
    USD,
    EUR,
    GBP,
    INR,
    PKR;

    public static final String REGEX = "USD|EUR|GBP|INR|PKR";
    public static final String VALIDATION_MESSAGE = "Currency must be one of: USD, EUR, GBP, INR, PKR";
}
