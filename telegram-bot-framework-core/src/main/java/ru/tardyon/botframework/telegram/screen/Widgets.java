package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public final class Widgets {

    private Widgets() {
    }

    public static Widget line(String text) {
        Objects.requireNonNull(text, "text must not be null");
        return (builder, context) -> builder.line(text);
    }

    public static Widget text(String text) {
        Objects.requireNonNull(text, "text must not be null");
        return (builder, context) -> builder.append(text);
    }

    public static Widget replyMarkup(ReplyMarkup replyMarkup) {
        return (builder, context) -> builder.replyMarkup(replyMarkup);
    }
}
