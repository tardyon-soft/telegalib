package ru.tardyon.botframework.telegram.api.model.command;

import java.util.Objects;

public record BotCommand(
    String command,
    String description
) {
    public BotCommand {
        Objects.requireNonNull(command, "command must not be null");
        Objects.requireNonNull(description, "description must not be null");
    }
}
