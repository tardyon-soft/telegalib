package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddlewareChain;

public final class ScreenMiddleware implements UpdateMiddleware {

    private final ScreenEngine screenEngine;

    public ScreenMiddleware(ScreenEngine screenEngine) {
        this.screenEngine = Objects.requireNonNull(screenEngine, "screenEngine must not be null");
    }

    @Override
    public void handle(UpdateContext updateContext, UpdateMiddlewareChain chain) {
        Objects.requireNonNull(updateContext, "updateContext must not be null");
        Objects.requireNonNull(chain, "chain must not be null");
        boolean handled = screenEngine.handle(updateContext);
        if (!handled) {
            chain.proceed(updateContext);
        }
    }
}
