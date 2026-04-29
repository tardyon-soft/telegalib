package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record SetChatDescriptionRequest(@JsonProperty("chat_id") Object chatId, String description) {
    public SetChatDescriptionRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        if (description != null && description.length() > 255) throw new IllegalArgumentException("description length must be <= 255");
    }
}
