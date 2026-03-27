package ru.tardyon.botframework.telegram.dispatcher.middleware;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

class DefaultDispatcherMiddlewareTest {

    @Test
    void executesMiddlewaresInDeterministicOrder() {
        List<String> events = new ArrayList<>();
        Router router = new Router()
            .message(Filters.any(), (ctx, message) -> events.add("handler"));

        UpdateMiddleware mw1 = (ctx, chain) -> {
            events.add("mw1:before");
            chain.proceed(ctx);
            events.add("mw1:after");
        };
        UpdateMiddleware mw2 = (ctx, chain) -> {
            events.add("mw2:before");
            chain.proceed(ctx);
            events.add("mw2:after");
        };

        DefaultDispatcher dispatcher = new DefaultDispatcher(router, List.of(mw1, mw2));
        dispatcher.dispatch(new UpdateContext(messageUpdate("ping")));

        assertEquals(List.of("mw1:before", "mw2:before", "handler", "mw2:after", "mw1:after"), events);
    }

    @Test
    void supportsShortCircuitWithoutHandlerExecution() {
        AtomicInteger handlerCalls = new AtomicInteger();
        Router router = new Router()
            .message(Filters.any(), (ctx, message) -> handlerCalls.incrementAndGet());

        UpdateMiddleware stopMiddleware = (ctx, chain) -> {
            ctx.setAttribute("blocked", true);
            // no proceed -> short-circuit
        };

        DefaultDispatcher dispatcher = new DefaultDispatcher(router, List.of(stopMiddleware));
        UpdateContext context = new UpdateContext(messageUpdate("ping"));

        dispatcher.dispatch(context);

        assertEquals(0, handlerCalls.get());
        assertTrue(Boolean.TRUE.equals(context.getAttribute("blocked")));
    }

    @Test
    void errorBoundaryMiddlewareCanSwallowHandlerError() {
        AtomicInteger errorCount = new AtomicInteger();
        Router router = new Router()
            .message(Filters.any(), (ctx, message) -> {
                throw new RuntimeException("boom");
            });

        ErrorBoundaryUpdateMiddleware boundary = new ErrorBoundaryUpdateMiddleware(
            error -> errorCount.incrementAndGet(),
            false
        );

        DefaultDispatcher dispatcher = new DefaultDispatcher(router, List.of(boundary));
        dispatcher.dispatch(new UpdateContext(messageUpdate("ping")));

        assertEquals(1, errorCount.get());
    }

    @Test
    void loggingMiddlewareProducesMessage() {
        List<String> logs = new ArrayList<>();
        Router router = new Router().message(Filters.any(), (ctx, message) -> {
        });

        DefaultDispatcher dispatcher = new DefaultDispatcher(
            router,
            List.of(new LoggingUpdateMiddleware(logs::add))
        );

        dispatcher.dispatch(new UpdateContext(messageUpdate("ping")));

        assertFalse(logs.isEmpty());
    }

    private static Update messageUpdate(String text) {
        Message message = new Message(
            1,
            null,
            new Chat(100L, "private", null, null, null, null, null),
            1,
            text,
            null,
            null,
            null
        );
        return new Update(1L, message, null, null, null, null, null, null);
    }
}
