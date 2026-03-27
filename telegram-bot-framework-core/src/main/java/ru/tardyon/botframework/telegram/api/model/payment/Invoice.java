package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Invoice(
    String title,
    String description,
    @JsonProperty("start_parameter") String startParameter,
    String currency,
    @JsonProperty("total_amount") Integer totalAmount
) {
}
