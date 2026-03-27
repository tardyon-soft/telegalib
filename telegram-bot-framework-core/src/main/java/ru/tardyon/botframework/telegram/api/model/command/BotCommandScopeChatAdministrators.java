package ru.tardyon.botframework.telegram.api.model.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record BotCommandScopeChatAdministrators(
    String type,
    @JsonProperty("chat_id") Object chatId
) implements BotCommandScope {

    public BotCommandScopeChatAdministrators(Object chatId) {
        this("chat_administrators", chatId);
    }

    public BotCommandScopeChatAdministrators {
        if (!"chat_administrators".equals(type)) {
            throw new IllegalArgumentException("type must be 'chat_administrators'");
        }
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
