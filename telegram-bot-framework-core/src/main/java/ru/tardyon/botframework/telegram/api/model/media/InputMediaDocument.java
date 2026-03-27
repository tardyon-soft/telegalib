package ru.tardyon.botframework.telegram.api.model.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record InputMediaDocument(
    String type,
    InputFile media,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities
) implements InputMedia {

    public InputMediaDocument {
        if (!"document".equals(type)) {
            throw new IllegalArgumentException("type must be 'document'");
        }
        Objects.requireNonNull(media, "media must not be null");
        captionEntities = captionEntities == null ? null : List.copyOf(captionEntities);
    }

    public InputMediaDocument(InputFile media) {
        this("document", media, null, null, null);
    }

    public static InputMediaDocument of(InputFile media) {
        return new InputMediaDocument(media);
    }
}
