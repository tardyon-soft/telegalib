package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetChatMenuButtonRequest(
    @JsonProperty("chat_id") Long chatId
) {
}
