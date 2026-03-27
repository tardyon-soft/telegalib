package ru.tardyon.botframework.telegram.spring.boot.widget;

import ru.tardyon.botframework.telegram.screen.ScreenContext;

public record WidgetContext(
    ScreenContext screenContext,
    String widgetId,
    String action,
    String payload
) {
}
