package ru.tardyon.botframework.telegram.api.model.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record InputMediaPhoto(
    String type,
    InputFile media,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities
) implements InputMedia {

    public InputMediaPhoto {
        if (!"photo".equals(type)) {
            throw new IllegalArgumentException("type must be 'photo'");
        }
        Objects.requireNonNull(media, "media must not be null");
        captionEntities = captionEntities == null ? null : List.copyOf(captionEntities);
    }

    public InputMediaPhoto(InputFile media) {
        this("photo", media, null, null, null);
    }

    public static InputMediaPhoto of(InputFile media) {
        return new InputMediaPhoto(media);
    }
}
