package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LabeledPrice(String label, Integer amount) {

    public LabeledPrice {
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
    }
}
