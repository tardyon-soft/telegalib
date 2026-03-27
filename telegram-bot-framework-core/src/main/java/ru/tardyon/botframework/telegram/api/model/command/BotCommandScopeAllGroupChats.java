package ru.tardyon.botframework.telegram.api.model.command;

public record BotCommandScopeAllGroupChats(String type) implements BotCommandScope {

    public BotCommandScopeAllGroupChats() {
        this("all_group_chats");
    }

    public BotCommandScopeAllGroupChats {
        if (!"all_group_chats".equals(type)) {
            throw new IllegalArgumentException("type must be 'all_group_chats'");
        }
    }
}
