package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record EditForumTopicRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_thread_id") Integer messageThreadId,
    String name,
    @JsonProperty("icon_custom_emoji_id") String iconCustomEmojiId
) {
    public EditForumTopicRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(messageThreadId, "messageThreadId must not be null");
    }
}
