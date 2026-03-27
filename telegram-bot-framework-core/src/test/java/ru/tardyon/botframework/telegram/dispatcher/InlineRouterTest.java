package ru.tardyon.botframework.telegram.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.ChosenInlineResult;
import ru.tardyon.botframework.telegram.api.model.InlineQuery;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

class InlineRouterTest {

    @Test
    void routesInlineQueryAndChosenInlineResult() {
        Router router = new Router();
        AtomicInteger inlineHits = new AtomicInteger();
        AtomicInteger chosenHits = new AtomicInteger();

        router.inlineQuery(Filters.any(), (ctx, inlineQuery) -> inlineHits.incrementAndGet());
        router.chosenInlineResult(Filters.any(), (ctx, chosenInlineResult) -> chosenHits.incrementAndGet());

        InlineQuery inlineQuery = new InlineQuery(
            "iq-1",
            new User(1L, false, "A", null, "a", "en", null, null, null),
            null,
            "java",
            "",
            "sender"
        );
        ChosenInlineResult chosenInlineResult = new ChosenInlineResult(
            "res-1",
            new User(1L, false, "A", null, "a", "en", null, null, null),
            null,
            null,
            "java"
        );

        router.route(new UpdateContext(new Update(1L, null, null, null, null, null, inlineQuery, null)));
        router.route(new UpdateContext(new Update(2L, null, null, null, null, null, null, chosenInlineResult)));

        assertEquals(1, inlineHits.get());
        assertEquals(1, chosenHits.get());
    }
}
