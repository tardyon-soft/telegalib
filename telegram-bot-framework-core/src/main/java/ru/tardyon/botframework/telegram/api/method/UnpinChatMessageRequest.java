package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record UnpinChatMessageRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_id") Integer messageId
) {
    public UnpinChatMessageRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
