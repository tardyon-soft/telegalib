package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record ForwardMessageRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("from_chat_id") Object fromChatId,
    @JsonProperty("message_id") Integer messageId
) {
    public ForwardMessageRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(fromChatId, "fromChatId must not be null");
        Objects.requireNonNull(messageId, "messageId must not be null");
    }
}
