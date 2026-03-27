package ru.tardyon.botframework.telegram.dispatcher.handler;

import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface PreCheckoutQueryHandler {

    void handle(UpdateContext context, PreCheckoutQuery preCheckoutQuery);
}
