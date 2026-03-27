package ru.tardyon.botframework.telegram.api.model.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record InputMediaAudio(
    String type,
    InputFile media,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities
) implements InputMedia {

    public InputMediaAudio {
        if (!"audio".equals(type)) {
            throw new IllegalArgumentException("type must be 'audio'");
        }
        Objects.requireNonNull(media, "media must not be null");
        captionEntities = captionEntities == null ? null : List.copyOf(captionEntities);
    }

    public InputMediaAudio(InputFile media) {
        this("audio", media, null, null, null);
    }

    public static InputMediaAudio of(InputFile media) {
        return new InputMediaAudio(media);
    }
}
