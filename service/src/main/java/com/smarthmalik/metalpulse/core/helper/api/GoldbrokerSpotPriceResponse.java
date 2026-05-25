package com.smarthmalik.metalpulse.core.helper.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoldbrokerSpotPriceResponse(

        @JsonProperty("weight_unit")
        String weightUnit,

        @JsonProperty("value")
        BigDecimal price,

        BigDecimal bid,
        BigDecimal ask,

        @JsonProperty("performance")
        BigDecimal change
) {}
