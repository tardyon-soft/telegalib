package ru.tardyon.botframework.telegram.api.model.markup;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

public record KeyboardButton(
    String text,
    @JsonProperty("web_app") WebAppInfo webApp
) {
    public KeyboardButton {
        Objects.requireNonNull(text, "text must not be null");
    }

    public static KeyboardButton text(String text) {
        return new KeyboardButton(text, null);
    }

    public static KeyboardButton webApp(String text, WebAppInfo webApp) {
        Objects.requireNonNull(webApp, "webApp must not be null");
        return new KeyboardButton(text, webApp);
    }
}
