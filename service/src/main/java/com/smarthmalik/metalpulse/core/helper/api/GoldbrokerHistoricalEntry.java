package com.smarthmalik.metalpulse.core.helper.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoldbrokerHistoricalEntry(
        String date,
        @JsonProperty("close") BigDecimal price
) {}
