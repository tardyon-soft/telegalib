package ru.tardyon.botframework.telegram.api.model.menu;

public record MenuButtonCommands(String type) implements MenuButton {

    public MenuButtonCommands() {
        this("commands");
    }

    public MenuButtonCommands {
        if (!"commands".equals(type)) {
            throw new IllegalArgumentException("type must be 'commands'");
        }
    }
}
