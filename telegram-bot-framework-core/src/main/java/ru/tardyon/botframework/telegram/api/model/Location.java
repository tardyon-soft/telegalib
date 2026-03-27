package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Location(
    double latitude,
    double longitude,
    @JsonProperty("horizontal_accuracy") Double horizontalAccuracy,
    @JsonProperty("live_period") Integer livePeriod,
    Integer heading,
    @JsonProperty("proximity_alert_radius") Integer proximityAlertRadius
) {
}
