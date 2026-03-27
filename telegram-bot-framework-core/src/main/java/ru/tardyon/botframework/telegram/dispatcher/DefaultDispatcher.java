package ru.tardyon.botframework.telegram.dispatcher;

import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticErrorEvent;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticsHooks;
import ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingFinishedEvent;
import ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingStartedEvent;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddlewareChain;

public class DefaultDispatcher implements Dispatcher {

    private final Router router;
    private final List<UpdateMiddleware> middlewares;
    private final DiagnosticsHooks diagnosticsHooks;

    public DefaultDispatcher(Router router) {
        this(router, List.of(), DiagnosticsHooks.noop());
    }

    public DefaultDispatcher(Router router, List<UpdateMiddleware> middlewares) {
        this(router, middlewares, DiagnosticsHooks.noop());
    }

    public DefaultDispatcher(Router router, List<UpdateMiddleware> middlewares, DiagnosticsHooks diagnosticsHooks) {
        this.router = Objects.requireNonNull(router, "router must not be null");
        this.middlewares = List.copyOf(Objects.requireNonNull(middlewares, "middlewares must not be null"));
        this.diagnosticsHooks = Objects.requireNonNull(diagnosticsHooks, "diagnosticsHooks must not be null");
    }

    public Router router() {
        return router;
    }

    @Override
    public void dispatch(UpdateContext updateContext) {
        String correlationId = updateContext.<String>findAttribute(DiagnosticsHooks.CORRELATION_ID_ATTR)
            .orElseGet(() -> {
                String generated = diagnosticsHooks.newCorrelationId();
                updateContext.setAttribute(DiagnosticsHooks.CORRELATION_ID_ATTR, generated);
                return generated;
            });
        String source = updateContext.<String>findAttribute(DiagnosticsHooks.UPDATE_SOURCE_ATTR).orElse("DISPATCH");
        long startedNanos = System.nanoTime();

        diagnosticsHooks.onUpdateStarted(new UpdateProcessingStartedEvent(
            correlationId,
            updateContext.getUpdate().updateId(),
            updateContext.getUpdateType(),
            source,
            System.currentTimeMillis()
        ));

        RuntimeException failure = null;
        try {
            new DefaultUpdateMiddlewareChain(middlewares, 0, router::route).proceed(updateContext);
        } catch (RuntimeException e) {
            failure = e;
            throw e;
        } finally {
            diagnosticsHooks.onUpdateFinished(new UpdateProcessingFinishedEvent(
                correlationId,
                updateContext.getUpdate().updateId(),
                updateContext.getUpdateType(),
                source,
                Math.max(0L, (System.nanoTime() - startedNanos) / 1_000_000L),
                failure == null
            ));
            if (failure != null) {
                diagnosticsHooks.onError(new DiagnosticErrorEvent(
                    correlationId,
                    "dispatcher",
                    "dispatch",
                    updateContext.getUpdate().updateId(),
                    null,
                    failure
                ));
            }
        }
    }

    private record DefaultUpdateMiddlewareChain(
        List<UpdateMiddleware> middlewares,
        int index,
        UpdateMiddlewareChain terminal
    ) implements UpdateMiddlewareChain {

        @Override
        public void proceed(UpdateContext updateContext) {
            if (index >= middlewares.size()) {
                terminal.proceed(updateContext);
                return;
            }
            UpdateMiddleware middleware = middlewares.get(index);
            UpdateMiddlewareChain next = new DefaultUpdateMiddlewareChain(middlewares, index + 1, terminal);
            middleware.handle(updateContext, next);
        }
    }
}
