package com.smarthmalik.metalpulse.exception;

public class UnsupportedCurrencyException extends RuntimeException {

    public UnsupportedCurrencyException(String currency) {
        super("Unsupported currency: " + currency);
    }
}
