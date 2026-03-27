package ru.tardyon.botframework.telegram.api.model.menu;

import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

public final class MenuButtons {

    private MenuButtons() {
    }

    public static MenuButtonDefault defaultButton() {
        return new MenuButtonDefault();
    }

    public static MenuButtonCommands commandsButton() {
        return new MenuButtonCommands();
    }

    public static MenuButtonWebApp webAppButton(String text, String url) {
        return MenuButtonWebApp.of(text, new WebAppInfo(url));
    }
}
