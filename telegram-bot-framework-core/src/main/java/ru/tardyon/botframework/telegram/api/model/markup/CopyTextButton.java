package ru.tardyon.botframework.telegram.api.model.markup;

import java.util.Objects;

public record CopyTextButton(String text) {

    public CopyTextButton {
        Objects.requireNonNull(text, "text must not be null");
        if (text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
        if (text.length() > 256) {
            throw new IllegalArgumentException("text length must be <= 256");
        }
    }
}
