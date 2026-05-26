package com.smarthmalik.metalpulse.exception;

public class UnsupportedCurrencyException extends RuntimeException {

    public UnsupportedCurrencyException(String currency) {
        this(currency, null);
    }

    public UnsupportedCurrencyException(String currency, String reason) {
        super("Unsupported currency: " + currency + (reason == null || reason.isBlank() ? "" : " (" + reason + ")"));
    }
}
