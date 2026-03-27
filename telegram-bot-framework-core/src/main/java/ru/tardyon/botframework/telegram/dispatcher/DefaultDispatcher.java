package ru.tardyon.botframework.telegram.dispatcher;

import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddlewareChain;

public class DefaultDispatcher implements Dispatcher {

    private final Router router;
    private final List<UpdateMiddleware> middlewares;

    public DefaultDispatcher(Router router) {
        this(router, List.of());
    }

    public DefaultDispatcher(Router router, List<UpdateMiddleware> middlewares) {
        this.router = Objects.requireNonNull(router, "router must not be null");
        this.middlewares = List.copyOf(Objects.requireNonNull(middlewares, "middlewares must not be null"));
    }

    public Router router() {
        return router;
    }

    @Override
    public void dispatch(UpdateContext updateContext) {
        new DefaultUpdateMiddlewareChain(middlewares, 0, router::route).proceed(updateContext);
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
