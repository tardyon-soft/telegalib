package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Sticker(
    @JsonProperty("file_id") String fileId,
    @JsonProperty("file_unique_id") String fileUniqueId,
    String type,
    Integer width,
    Integer height,
    @JsonProperty("is_animated") Boolean animated,
    @JsonProperty("is_video") Boolean video,
    PhotoSize thumbnail,
    String emoji,
    @JsonProperty("set_name") String setName,
    @JsonProperty("custom_emoji_id") String customEmojiId,
    @JsonProperty("file_size") Long fileSize
) {
}
