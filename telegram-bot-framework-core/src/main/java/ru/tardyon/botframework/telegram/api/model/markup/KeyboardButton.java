package ru.tardyon.botframework.telegram.api.model.markup;

import java.util.Objects;

public record KeyboardButton(String text) {
    public KeyboardButton {
        Objects.requireNonNull(text, "text must not be null");
    }

    public static KeyboardButton text(String text) {
        return new KeyboardButton(text);
    }
}
