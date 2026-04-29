package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record UnbanChatMemberRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("user_id") long userId,
    @JsonProperty("only_if_banned") Boolean onlyIfBanned
) {
    public UnbanChatMemberRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
