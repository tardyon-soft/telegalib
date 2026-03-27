package ru.tardyon.botframework.telegram.spring.boot.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardButton;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public final class WidgetButtons {

    private WidgetButtons() {
    }

    public static <T> InlineKeyboardMarkup objectList(
        String widgetId,
        String action,
        List<T> items,
        Function<T, String> labelMapper,
        Function<T, String> payloadMapper
    ) {
        Objects.requireNonNull(widgetId, "widgetId must not be null");
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(items, "items must not be null");
        Objects.requireNonNull(labelMapper, "labelMapper must not be null");
        Objects.requireNonNull(payloadMapper, "payloadMapper must not be null");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (T item : items) {
            String label = labelMapper.apply(item);
            String payload = payloadMapper.apply(item);
            rows.add(List.of(InlineKeyboardButton.callback(label, AnnotatedWidgetRegistry.encodeCallback(widgetId, action, payload))));
        }
        return new InlineKeyboardMarkup(rows);
    }
}
