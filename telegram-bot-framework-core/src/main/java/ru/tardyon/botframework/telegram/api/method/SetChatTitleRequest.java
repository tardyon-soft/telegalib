package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record SetChatTitleRequest(
    @JsonProperty("chat_id") Object chatId,
    String title
) {
    public SetChatTitleRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }
}
