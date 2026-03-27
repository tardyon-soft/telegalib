package ru.tardyon.botframework.telegram.dispatcher.middleware;

import java.util.Objects;
import java.util.function.Consumer;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

public class ErrorBoundaryUpdateMiddleware implements UpdateMiddleware {

    private final Consumer<Throwable> errorConsumer;
    private final boolean rethrow;

    public ErrorBoundaryUpdateMiddleware(Consumer<Throwable> errorConsumer) {
        this(errorConsumer, true);
    }

    public ErrorBoundaryUpdateMiddleware(Consumer<Throwable> errorConsumer, boolean rethrow) {
        this.errorConsumer = Objects.requireNonNull(errorConsumer, "errorConsumer must not be null");
        this.rethrow = rethrow;
    }

    @Override
    public void handle(UpdateContext updateContext, UpdateMiddlewareChain chain) {
        try {
            chain.proceed(updateContext);
        } catch (RuntimeException e) {
            errorConsumer.accept(e);
            if (rethrow) {
                throw e;
            }
        }
    }
}
