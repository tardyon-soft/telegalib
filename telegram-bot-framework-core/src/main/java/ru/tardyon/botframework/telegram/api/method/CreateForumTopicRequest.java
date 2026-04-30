package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record CreateForumTopicRequest(
    @JsonProperty("chat_id") Object chatId,
    String name,
    @JsonProperty("icon_color") Integer iconColor,
    @JsonProperty("icon_custom_emoji_id") String iconCustomEmojiId
) {
    public CreateForumTopicRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
