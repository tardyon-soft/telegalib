package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Telegram entity in message text. Offsets and lengths are measured in UTF-16 code units.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageEntity(
    String type,
    Integer offset,
    Integer length,
    String url,
    User user,
    String language,
    @JsonProperty("custom_emoji_id") String customEmojiId
) {
}
