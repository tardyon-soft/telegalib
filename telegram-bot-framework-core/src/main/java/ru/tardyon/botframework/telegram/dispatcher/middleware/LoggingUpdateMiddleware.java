package ru.tardyon.botframework.telegram.dispatcher.middleware;

import java.util.Objects;
import java.util.function.Consumer;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

public class LoggingUpdateMiddleware implements UpdateMiddleware {

    private final Consumer<String> logger;

    public LoggingUpdateMiddleware(Consumer<String> logger) {
        this.logger = Objects.requireNonNull(logger, "logger must not be null");
    }

    @Override
    public void handle(UpdateContext updateContext, UpdateMiddlewareChain chain) {
        logger.accept("Incoming update: type=" + updateContext.getUpdateType() + ", updateId=" + updateContext.getUpdate().updateId());
        chain.proceed(updateContext);
    }
}
