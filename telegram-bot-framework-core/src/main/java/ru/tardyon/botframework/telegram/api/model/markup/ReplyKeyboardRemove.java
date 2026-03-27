package ru.tardyon.botframework.telegram.api.model.markup;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReplyKeyboardRemove(
    @JsonProperty("remove_keyboard") boolean removeKeyboard
) implements ReplyMarkup {
    public ReplyKeyboardRemove() {
        this(true);
    }
}
