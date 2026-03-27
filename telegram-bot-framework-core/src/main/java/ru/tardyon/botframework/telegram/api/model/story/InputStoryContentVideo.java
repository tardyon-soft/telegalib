package ru.tardyon.botframework.telegram.api.model.story;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;

public record InputStoryContentVideo(
    InputFile video,
    Double duration,
    @JsonProperty("cover_frame_timestamp") Double coverFrameTimestamp,
    @JsonProperty("is_animation") Boolean isAnimation
) implements InputStoryContent {

    public InputStoryContentVideo {
        Objects.requireNonNull(video, "video must not be null");
    }

    @Override
    public String type() {
        return "video";
    }

    @Override
    public InputFile media() {
        return video;
    }

    public static InputStoryContentVideo of(InputFile video) {
        return new InputStoryContentVideo(video, null, null, null);
    }
}
