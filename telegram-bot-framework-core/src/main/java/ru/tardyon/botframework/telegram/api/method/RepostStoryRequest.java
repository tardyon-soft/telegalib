package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepostStoryRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("from_chat_id") Long fromChatId,
    @JsonProperty("from_story_id") Integer fromStoryId,
    @JsonProperty("active_period") Integer activePeriod,
    @JsonProperty("post_to_chat_page") Boolean postToChatPage,
    @JsonProperty("protect_content") Boolean protectContent
) {

    public RepostStoryRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (fromChatId == null) {
            throw new IllegalArgumentException("fromChatId must not be null");
        }
        if (fromStoryId == null) {
            throw new IllegalArgumentException("fromStoryId must not be null");
        }
        if (activePeriod == null) {
            throw new IllegalArgumentException("activePeriod must not be null");
        }
        if (activePeriod != 6 * 3600 && activePeriod != 12 * 3600 && activePeriod != 86400 && activePeriod != 2 * 86400) {
            throw new IllegalArgumentException("activePeriod must be one of 21600, 43200, 86400, 172800");
        }
    }
}
