package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record ForumTopicRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_thread_id") Integer messageThreadId
) {
    public ForumTopicRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(messageThreadId, "messageThreadId must not be null");
    }
}
