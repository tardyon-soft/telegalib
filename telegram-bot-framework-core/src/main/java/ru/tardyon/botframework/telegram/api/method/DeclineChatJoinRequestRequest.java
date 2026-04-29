package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record DeclineChatJoinRequestRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("user_id") long userId
) {
    public DeclineChatJoinRequestRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
