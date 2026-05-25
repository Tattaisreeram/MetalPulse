package com.smarthmalik.metalpulse.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(BigDecimal required, BigDecimal available) {
        super(String.format("Insufficient balance. Required: %.4f, Available: %.4f", required, available));
    }
}
