package ru.tardyon.botframework.telegram.api.model.markup;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public record InlineKeyboardMarkup(
    @JsonProperty("inline_keyboard") List<List<InlineKeyboardButton>> inlineKeyboard
) implements ReplyMarkup {
    public InlineKeyboardMarkup {
        Objects.requireNonNull(inlineKeyboard, "inlineKeyboard must not be null");
        inlineKeyboard = inlineKeyboard.stream().map(List::copyOf).toList();
    }
}
