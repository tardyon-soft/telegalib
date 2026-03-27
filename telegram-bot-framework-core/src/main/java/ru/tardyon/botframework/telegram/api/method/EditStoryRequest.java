package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.story.InputStoryContent;
import ru.tardyon.botframework.telegram.api.model.story.StoryArea;

public record EditStoryRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("story_id") Integer storyId,
    InputStoryContent content,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    List<StoryArea> areas
) {

    public EditStoryRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        Objects.requireNonNull(storyId, "storyId must not be null");
        Objects.requireNonNull(content, "content must not be null");
        if (caption != null && caption.length() > 2048) {
            throw new IllegalArgumentException("caption length must be in range 0..2048");
        }
        captionEntities = captionEntities == null ? null : List.copyOf(captionEntities);
        areas = areas == null ? null : List.copyOf(areas);
    }
}
