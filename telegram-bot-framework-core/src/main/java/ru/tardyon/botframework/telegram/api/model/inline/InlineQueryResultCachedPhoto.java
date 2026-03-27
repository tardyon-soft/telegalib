package ru.tardyon.botframework.telegram.api.model.inline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public record InlineQueryResultCachedPhoto(
    String type,
    String id,
    @JsonProperty("photo_file_id") String photoFileId,
    String title,
    String description,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup,
    @JsonProperty("input_message_content") InputMessageContent inputMessageContent
) implements InlineQueryResult {

    public InlineQueryResultCachedPhoto {
        requireType(type);
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(photoFileId, "photoFileId must not be null");
    }

    public InlineQueryResultCachedPhoto(String id, String photoFileId) {
        this("cached_photo", id, photoFileId, null, null, null, null, null, null, null);
    }

    private static void requireType(String type) {
        if (!"cached_photo".equals(type)) {
            throw new IllegalArgumentException("type must be 'cached_photo'");
        }
    }
}
