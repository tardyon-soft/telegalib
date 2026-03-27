package ru.tardyon.botframework.telegram.dispatcher.middleware;

import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface UpdateMiddleware {

    void handle(UpdateContext updateContext, UpdateMiddlewareChain chain);
}
