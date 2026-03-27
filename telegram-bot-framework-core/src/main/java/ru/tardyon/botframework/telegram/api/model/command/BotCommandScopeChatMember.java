package ru.tardyon.botframework.telegram.api.model.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record BotCommandScopeChatMember(
    String type,
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("user_id") long userId
) implements BotCommandScope {

    public BotCommandScopeChatMember(Object chatId, long userId) {
        this("chat_member", chatId, userId);
    }

    public BotCommandScopeChatMember {
        if (!"chat_member".equals(type)) {
            throw new IllegalArgumentException("type must be 'chat_member'");
        }
        Objects.requireNonNull(chatId, "chatId must not be null");
    }
}
