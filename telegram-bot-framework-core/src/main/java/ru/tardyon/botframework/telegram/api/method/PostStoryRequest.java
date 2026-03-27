package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.story.InputStoryContent;
import ru.tardyon.botframework.telegram.api.model.story.StoryArea;

public record PostStoryRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    InputStoryContent content,
    @JsonProperty("active_period") Integer activePeriod,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    List<StoryArea> areas,
    @JsonProperty("post_to_chat_page") Boolean postToChatPage,
    @JsonProperty("protect_content") Boolean protectContent
) {

    public PostStoryRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(activePeriod, "activePeriod must not be null");
        if (activePeriod != 6 * 3600 && activePeriod != 12 * 3600 && activePeriod != 86400 && activePeriod != 2 * 86400) {
            throw new IllegalArgumentException("activePeriod must be one of 21600, 43200, 86400, 172800");
        }
        if (caption != null && caption.length() > 2048) {
            throw new IllegalArgumentException("caption length must be in range 0..2048");
        }
        captionEntities = captionEntities == null ? null : List.copyOf(captionEntities);
        areas = areas == null ? null : List.copyOf(areas);
    }
}
