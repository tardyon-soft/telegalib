package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record EditGeneralForumTopicRequest(
    @JsonProperty("chat_id") Object chatId,
    String name
) {
    public EditGeneralForumTopicRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
