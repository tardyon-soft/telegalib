package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Gifts(
    List<Gift> gifts
) {

    public Gifts {
        gifts = gifts == null ? List.of() : List.copyOf(gifts);
    }
}
