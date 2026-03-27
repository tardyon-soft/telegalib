package ru.tardyon.botframework.telegram.dispatcher.handler;

import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface CallbackQueryHandler {

    void handle(UpdateContext context, CallbackQuery callbackQuery);
}
