package com.smarthmalik.metalpulse.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@Schema(description = "Request to buy, sell, or hold a precious metal")
public record TradeRequest(

        @NotBlank(message = "Metal symbol is required")
        @Pattern(regexp = "XAU|XAG|XPT|XPD", message = "Metal must be one of: XAU, XAG, XPT, XPD")
        @Schema(example = "XAU", description = "XAU=Gold  XAG=Silver  XPT=Platinum  XPD=Palladium")
        String metal,

        @NotBlank(message = "Currency code is required")
        @Schema(example = "PKR")
        String currency,

        @NotBlank(message = "Weight unit is required")
        @Pattern(regexp = "g|oz|kg|tola", message = "Weight unit must be one of: g, oz, kg, tola")
        @Schema(example = "g")
        String weightUnit,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.000001", message = "Quantity must be positive")
        @Schema(example = "10.5")
        BigDecimal quantity
) {}
