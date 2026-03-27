package ru.tardyon.botframework.telegram.api.model.command;

public record BotCommandScopeAllChatAdministrators(String type) implements BotCommandScope {

    public BotCommandScopeAllChatAdministrators() {
        this("all_chat_administrators");
    }

    public BotCommandScopeAllChatAdministrators {
        if (!"all_chat_administrators".equals(type)) {
            throw new IllegalArgumentException("type must be 'all_chat_administrators'");
        }
    }
}
