package ru.tardyon.botframework.telegram.dispatcher.command;

public record CommandContext(
    String command,
    String botUsername,
    String argsRaw
) {
    public boolean hasBotUsername() {
        return botUsername != null && !botUsername.isBlank();
    }

    public boolean hasArgs() {
        return argsRaw != null && !argsRaw.isBlank();
    }
}
