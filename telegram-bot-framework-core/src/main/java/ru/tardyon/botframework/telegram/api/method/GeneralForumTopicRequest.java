package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record GeneralForumTopicRequest(
    @JsonProperty("chat_id") Object chatId
) {
    public GeneralForumTopicRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
