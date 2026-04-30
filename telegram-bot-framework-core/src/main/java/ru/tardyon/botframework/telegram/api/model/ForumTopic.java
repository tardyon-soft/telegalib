package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ForumTopic(
    @JsonProperty("message_thread_id") Integer messageThreadId,
    String name,
    @JsonProperty("icon_color") Integer iconColor,
    @JsonProperty("icon_custom_emoji_id") String iconCustomEmojiId
) {
}
