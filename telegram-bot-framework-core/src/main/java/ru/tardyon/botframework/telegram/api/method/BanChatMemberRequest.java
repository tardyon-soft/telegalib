package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record BanChatMemberRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("user_id") long userId,
    @JsonProperty("until_date") Long untilDate,
    @JsonProperty("revoke_messages") Boolean revokeMessages
) {
    public BanChatMemberRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
