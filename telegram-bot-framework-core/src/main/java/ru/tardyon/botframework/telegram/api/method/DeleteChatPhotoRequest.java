package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record DeleteChatPhotoRequest(
    @JsonProperty("chat_id") Object chatId
) {
    public DeleteChatPhotoRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
