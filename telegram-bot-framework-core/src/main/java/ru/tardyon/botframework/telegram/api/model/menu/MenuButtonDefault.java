package ru.tardyon.botframework.telegram.api.model.menu;

public record MenuButtonDefault(String type) implements MenuButton {

    public MenuButtonDefault() {
        this("default");
    }

    public MenuButtonDefault {
        if (!"default".equals(type)) {
            throw new IllegalArgumentException("type must be 'default'");
        }
    }
}
