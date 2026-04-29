package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public record CopyMessagesRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("from_chat_id") Object fromChatId,
    @JsonProperty("message_ids") List<Integer> messageIds
) {
    public CopyMessagesRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(fromChatId, "fromChatId must not be null");
        Objects.requireNonNull(messageIds, "messageIds must not be null");
        if (messageIds.isEmpty()) {
            throw new IllegalArgumentException("messageIds must not be empty");
        }
        if (messageIds.size() > 100) {
            throw new IllegalArgumentException("messageIds size must be <= 100");
        }
        messageIds = List.copyOf(messageIds);
    }
}
