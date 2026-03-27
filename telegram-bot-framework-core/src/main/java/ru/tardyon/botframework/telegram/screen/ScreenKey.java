package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;

public record ScreenKey(
    String botId,
    long chatId
) {
    public ScreenKey {
        Objects.requireNonNull(botId, "botId must not be null");
        if (botId.isBlank()) {
            throw new IllegalArgumentException("botId must not be blank");
        }
    }
}
