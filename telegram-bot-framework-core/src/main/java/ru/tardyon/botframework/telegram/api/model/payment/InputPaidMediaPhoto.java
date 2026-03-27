package ru.tardyon.botframework.telegram.api.model.payment;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;

public record InputPaidMediaPhoto(
    String type,
    InputFile media
) implements InputPaidMedia {

    public InputPaidMediaPhoto {
        if (!"photo".equals(type)) {
            throw new IllegalArgumentException("type must be 'photo'");
        }
        Objects.requireNonNull(media, "media must not be null");
    }

    public InputPaidMediaPhoto(InputFile media) {
        this("photo", media);
    }

    public static InputPaidMediaPhoto of(InputFile media) {
        return new InputPaidMediaPhoto(media);
    }
}

