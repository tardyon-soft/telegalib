package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GiftBackground(
    @JsonProperty("center_color") Integer centerColor,
    @JsonProperty("edge_color") Integer edgeColor,
    @JsonProperty("text_color") Integer textColor
) {
}
