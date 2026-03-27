package ru.tardyon.botframework.telegram.api.model.story;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;

public record InputStoryContentPhoto(InputFile photo) implements InputStoryContent {

    public InputStoryContentPhoto {
        Objects.requireNonNull(photo, "photo must not be null");
    }

    @Override
    public String type() {
        return "photo";
    }

    @Override
    public InputFile media() {
        return photo;
    }

    public static InputStoryContentPhoto of(InputFile photo) {
        return new InputStoryContentPhoto(photo);
    }
}
