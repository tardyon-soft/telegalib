package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record GetChatAdministratorsRequest(
    @JsonProperty("chat_id") Object chatId
) {

    public GetChatAdministratorsRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
    }

    public static GetChatAdministratorsRequest of(long chatId) {
        return new GetChatAdministratorsRequest(chatId);
    }

    public static GetChatAdministratorsRequest of(String chatId) {
        return new GetChatAdministratorsRequest(chatId);
    }
}
