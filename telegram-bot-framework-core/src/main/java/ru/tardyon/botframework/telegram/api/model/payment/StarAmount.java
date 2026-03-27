package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StarAmount(
    Integer amount,
    @JsonProperty("nanostar_amount") Integer nanostarAmount
) {
}

