package ru.tardyon.botframework.telegram.dispatcher.middleware;

import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface UpdateMiddlewareChain {

    void proceed(UpdateContext updateContext);
}
