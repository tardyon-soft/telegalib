package ru.tardyon.botframework.telegram.api.model.payment;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;

public record InputPaidMediaVideo(
    String type,
    InputFile media
) implements InputPaidMedia {

    public InputPaidMediaVideo {
        if (!"video".equals(type)) {
            throw new IllegalArgumentException("type must be 'video'");
        }
        Objects.requireNonNull(media, "media must not be null");
    }

    public InputPaidMediaVideo(InputFile media) {
        this("video", media);
    }

    public static InputPaidMediaVideo of(InputFile media) {
        return new InputPaidMediaVideo(media);
    }
}

