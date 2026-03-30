package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record GetChatMemberCountRequest(
    @JsonProperty("chat_id") Object chatId
) {

    public GetChatMemberCountRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }

    public static GetChatMemberCountRequest of(long chatId) {
        return new GetChatMemberCountRequest(chatId);
    }

    public static GetChatMemberCountRequest of(String chatId) {
        return new GetChatMemberCountRequest(chatId);
    }
}
