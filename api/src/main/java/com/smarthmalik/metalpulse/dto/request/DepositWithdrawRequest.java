package com.smarthmalik.metalpulse.dto.request;

import java.math.BigDecimal;

import com.smarthmalik.metalpulse.SupportedCurrency;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Deposit or withdrawal amount with currency")
public record DepositWithdrawRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        @Schema(example = "50000.00")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = SupportedCurrency.REGEX, message = SupportedCurrency.VALIDATION_MESSAGE)
        @Schema(example = "USD")
        String currency
) {}
