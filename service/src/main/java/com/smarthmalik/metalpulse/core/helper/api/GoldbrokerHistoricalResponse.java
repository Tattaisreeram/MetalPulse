package com.smarthmalik.metalpulse.core.helper.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoldbrokerHistoricalResponse(
        @JsonProperty("_embedded") Embedded embedded
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Embedded(List<GoldbrokerHistoricalEntry> items) {}

    public List<GoldbrokerHistoricalEntry> data() {
        return embedded != null && embedded.items() != null ? embedded.items() : List.of();
    }
}
