package ru.tardyon.botframework.telegram.screen;

import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

public interface ScreenViewRenderer {

    void render(UpdateContext updateContext, ScreenStateContext screenStateContext, long chatId, ScreenView view);
}
