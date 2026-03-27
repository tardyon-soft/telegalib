package ru.tardyon.botframework.telegram.api.model.command;

public record BotCommandScopeDefault(String type) implements BotCommandScope {

    public BotCommandScopeDefault() {
        this("default");
    }

    public BotCommandScopeDefault {
        if (!"default".equals(type)) {
            throw new IllegalArgumentException("type must be 'default'");
        }
    }
}
