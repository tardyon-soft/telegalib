package ru.tardyon.botframework.telegram.api.model.inline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record InlineQueryResultsButton(
    String text,
    @JsonProperty("start_parameter") String startParameter
) {

    public InlineQueryResultsButton {
        Objects.requireNonNull(text, "text must not be null");
        if (text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
    }
}
