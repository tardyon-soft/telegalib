package ru.tardyon.botframework.telegram.screen;

import java.util.Optional;

public interface ScreenNavigator {

    void push(String screenId);

    void replace(String screenId);

    boolean back();

    void clear();

    Optional<String> currentScreenId();

    void renderCurrent();
}
