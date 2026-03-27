package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMedia;

public record SendPaidMediaRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("star_count") Integer starCount,
    List<InputPaidMedia> media,
    String payload,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("show_caption_above_media") Boolean showCaptionAboveMedia,
    @JsonProperty("disable_notification") Boolean disableNotification
) {

    public SendPaidMediaRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(starCount, "starCount must not be null");
        Objects.requireNonNull(media, "media must not be null");
        if (starCount < 1 || starCount > 25000) {
            throw new IllegalArgumentException("starCount must be in range 1..25000");
        }
        if (media.isEmpty() || media.size() > 10) {
            throw new IllegalArgumentException("media size must be in range 1..10");
        }
        media = List.copyOf(media);
        captionEntities = captionEntities == null ? null : List.copyOf(captionEntities);
    }
}

