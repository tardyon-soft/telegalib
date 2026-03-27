package ru.tardyon.botframework.telegram.spring.boot.widget;

import ru.tardyon.botframework.telegram.screen.ScreenContext;

@FunctionalInterface
public interface WidgetEffect {

    void apply(ScreenContext screenContext);
}
