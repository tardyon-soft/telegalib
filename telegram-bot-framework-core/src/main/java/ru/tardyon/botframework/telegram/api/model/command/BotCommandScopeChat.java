package ru.tardyon.botframework.telegram.api.model.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record BotCommandScopeChat(
    String type,
    @JsonProperty("chat_id") Object chatId
) implements BotCommandScope {

    public BotCommandScopeChat(Object chatId) {
        this("chat", chatId);
    }

    public BotCommandScopeChat {
        if (!"chat".equals(type)) {
            throw new IllegalArgumentException("type must be 'chat'");
        }
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
