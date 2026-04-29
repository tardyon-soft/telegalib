package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record GetChatRequest(
    @JsonProperty("chat_id") Object chatId
) {

    public GetChatRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }

    public static GetChatRequest of(long chatId) {
        return new GetChatRequest(chatId);
    }

    public static GetChatRequest of(String chatId) {
        return new GetChatRequest(chatId);
    }
}
