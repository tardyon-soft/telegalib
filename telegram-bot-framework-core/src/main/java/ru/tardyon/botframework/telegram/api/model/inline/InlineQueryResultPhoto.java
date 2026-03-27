package ru.tardyon.botframework.telegram.api.model.inline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public record InlineQueryResultPhoto(
    String type,
    String id,
    @JsonProperty("photo_url") String photoUrl,
    @JsonProperty("thumbnail_url") String thumbnailUrl,
    @JsonProperty("photo_width") Integer photoWidth,
    @JsonProperty("photo_height") Integer photoHeight,
    String title,
    String description,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup,
    @JsonProperty("input_message_content") InputMessageContent inputMessageContent
) implements InlineQueryResult {

    public InlineQueryResultPhoto {
        requireType(type);
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(photoUrl, "photoUrl must not be null");
        Objects.requireNonNull(thumbnailUrl, "thumbnailUrl must not be null");
    }

    public InlineQueryResultPhoto(String id, String photoUrl, String thumbnailUrl) {
        this("photo", id, photoUrl, thumbnailUrl, null, null, null, null, null, null, null, null, null);
    }

    private static void requireType(String type) {
        if (!"photo".equals(type)) {
            throw new IllegalArgumentException("type must be 'photo'");
        }
    }
}
