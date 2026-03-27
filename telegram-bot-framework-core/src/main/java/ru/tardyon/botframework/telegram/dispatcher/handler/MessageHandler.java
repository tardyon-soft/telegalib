package ru.tardyon.botframework.telegram.dispatcher.handler;

import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface MessageHandler {

    void handle(UpdateContext context, Message message);
}
