package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record DeleteMessageRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_id") Integer messageId
) {
    public DeleteMessageRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(messageId, "messageId must not be null");
    }

    public static DeleteMessageRequest of(long chatId, int messageId) {
        return new DeleteMessageRequest(chatId, messageId);
    }

    public static DeleteMessageRequest of(String chatId, int messageId) {
        return new DeleteMessageRequest(chatId, messageId);
    }
}
