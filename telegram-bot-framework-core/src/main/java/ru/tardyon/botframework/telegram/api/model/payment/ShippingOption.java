package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShippingOption(String id, String title, List<LabeledPrice> prices) {

    public ShippingOption {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(prices, "prices must not be null");
        if (prices.isEmpty()) {
            throw new IllegalArgumentException("prices must not be empty");
        }
        prices = List.copyOf(prices);
    }
}
