package ru.tardyon.botframework.telegram.api.model.command;

public record BotCommandScopeAllPrivateChats(String type) implements BotCommandScope {

    public BotCommandScopeAllPrivateChats() {
        this("all_private_chats");
    }

    public BotCommandScopeAllPrivateChats {
        if (!"all_private_chats".equals(type)) {
            throw new IllegalArgumentException("type must be 'all_private_chats'");
        }
    }
}
