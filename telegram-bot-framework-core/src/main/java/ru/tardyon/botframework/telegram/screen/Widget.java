package ru.tardyon.botframework.telegram.screen;

@FunctionalInterface
public interface Widget {

    void apply(ScreenView.Builder builder, ScreenContext context);
}
