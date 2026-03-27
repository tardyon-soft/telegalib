package ru.tardyon.botframework.telegram.dispatcher.command;

import java.util.Objects;
import java.util.Optional;
import ru.tardyon.botframework.telegram.api.model.Message;

public final class CommandParser {

    private CommandParser() {
    }

    public static Optional<CommandContext> parse(Message message) {
        if (message == null || message.text() == null) {
            return Optional.empty();
        }

        String text = message.text().stripLeading();
        if (!text.startsWith("/")) {
            return Optional.empty();
        }

        int end = 1;
        while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
            end++;
        }

        String commandToken = text.substring(1, end);
        if (commandToken.isEmpty()) {
            return Optional.empty();
        }

        String[] parts = commandToken.split("@", 2);
        String command = parts[0];
        String botUsername = parts.length == 2 ? parts[1] : null;

        if (command.isEmpty()) {
            return Optional.empty();
        }

        String args = end < text.length() ? text.substring(end).stripLeading() : "";
        return Optional.of(new CommandContext(command, botUsername, args));
    }

    public static String normalizeExpectedCommand(String command) {
        Objects.requireNonNull(command, "command must not be null");
        String normalized = command.strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("command must not be blank");
        }
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("command must not be blank");
        }
        return normalized;
    }
}
