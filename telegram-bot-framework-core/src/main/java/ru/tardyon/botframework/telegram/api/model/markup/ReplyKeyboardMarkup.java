package ru.tardyon.botframework.telegram.api.model.markup;

import java.util.List;
import java.util.Objects;

public record ReplyKeyboardMarkup(
    List<List<KeyboardButton>> keyboard
) implements ReplyMarkup {
    public ReplyKeyboardMarkup {
        Objects.requireNonNull(keyboard, "keyboard must not be null");
        keyboard = keyboard.stream().map(List::copyOf).toList();
    }
}
