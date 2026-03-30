package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record GetChatMemberRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("user_id") long userId
) {

    public GetChatMemberRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }

    public static GetChatMemberRequest of(long chatId, long userId) {
        return new GetChatMemberRequest(chatId, userId);
    }

    public static GetChatMemberRequest of(String chatId, long userId) {
        return new GetChatMemberRequest(chatId, userId);
    }
}
