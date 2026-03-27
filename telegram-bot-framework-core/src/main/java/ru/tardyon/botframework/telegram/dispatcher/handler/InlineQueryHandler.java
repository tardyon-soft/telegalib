package ru.tardyon.botframework.telegram.dispatcher.handler;

import ru.tardyon.botframework.telegram.api.model.InlineQuery;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface InlineQueryHandler {

    void handle(UpdateContext context, InlineQuery inlineQuery);
}
