package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteStoryRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("story_id") Integer storyId
) {

    public DeleteStoryRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (storyId == null) {
            throw new IllegalArgumentException("storyId must not be null");
        }
    }
}
