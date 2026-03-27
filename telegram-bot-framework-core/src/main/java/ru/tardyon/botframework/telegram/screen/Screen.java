package ru.tardyon.botframework.telegram.screen;

import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Message;

public interface Screen {

    String id();

    ScreenView render(ScreenContext context);

    default ScreenAction onMessage(ScreenContext context, Message message) {
        return ScreenAction.unhandled();
    }

    default ScreenAction onCallbackQuery(ScreenContext context, CallbackQuery callbackQuery) {
        return ScreenAction.unhandled();
    }
}
