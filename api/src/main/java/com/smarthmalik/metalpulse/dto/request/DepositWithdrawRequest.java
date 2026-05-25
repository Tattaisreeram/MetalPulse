package com.smarthmalik.metalpulse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Deposit or withdrawal amount with currency")
public record DepositWithdrawRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        @Schema(example = "50000.00")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Schema(example = "USD", allowableValues = {"PKR", "USD", "EUR", "GBP"})
        String currency
) {}
