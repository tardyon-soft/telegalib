package ru.tardyon.botframework.telegram.api.model.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

public record MenuButtonWebApp(
    String type,
    String text,
    @JsonProperty("web_app") WebAppInfo webApp
) implements MenuButton {

    public MenuButtonWebApp {
        if (!"web_app".equals(type)) {
            throw new IllegalArgumentException("type must be 'web_app'");
        }
        Objects.requireNonNull(text, "text must not be null");
        if (text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
        Objects.requireNonNull(webApp, "webApp must not be null");
    }

    public static MenuButtonWebApp of(String text, WebAppInfo webApp) {
        return new MenuButtonWebApp("web_app", text, webApp);
    }
}
