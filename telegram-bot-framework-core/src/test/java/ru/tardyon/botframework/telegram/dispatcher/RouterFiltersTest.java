package ru.tardyon.botframework.telegram.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filter;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;

class RouterFiltersTest {

    @Test
    void routesMessageAndCallbackByFilters() {
        Router router = new Router();
        AtomicInteger commandHits = new AtomicInteger();
        AtomicInteger callbackHits = new AtomicInteger();

        router.message(Filters.command("start"), (ctx, msg) -> commandHits.incrementAndGet());
        router.callbackQuery(Filters.callbackDataStartsWith("menu:"), (ctx, cb) -> callbackHits.incrementAndGet());

        Message message = new Message(
            1,
            new User(42L, false, "A", null, "alice", "en", null, null, null),
            new Chat(100L, "private", null, null, "A", null, null),
            1,
            "/start",
            null,
            null,
            null
        );

        CallbackQuery callbackQuery = new CallbackQuery(
            "cb1",
            new User(42L, false, "A", null, "alice", "en", null, null, null),
            null,
            null,
            "chat-instance",
            "menu:settings",
            null
        );

        router.route(new UpdateContext(new Update(1L, message, null, null, null, null, null, null)));
        router.route(new UpdateContext(new Update(2L, null, null, null, null, callbackQuery, null, null)));

        assertEquals(1, commandHits.get());
        assertEquals(1, callbackHits.get());
    }

    @Test
    void commandFilterSupportsBasicStartAndHelp() {
        Message start = new Message(1, null, null, 1, "/start", null, null, null);
        Message help = new Message(2, null, null, 1, "/help some args", null, null, null);
        Message withSuffix = new Message(3, null, null, 1, "/start@my_bot", null, null, null);

        assertTrue(Filters.command("start").test(start));
        assertTrue(Filters.commands("start", "help").test(help));
        assertTrue(Filters.command("start").test(withSuffix));
    }

    @Test
    void commandContextParsesBotUsernameAndArgs() {
        Message message = new Message(1, null, null, 1, "/start@my_bot foo bar", null, null, null);

        var commandContext = Filters.commandContext(message).orElseThrow();

        assertEquals("start", commandContext.command());
        assertEquals("my_bot", commandContext.botUsername());
        assertEquals("foo bar", commandContext.argsRaw());
    }

    @Test
    void filterCombinatorsWork() {
        Filter<Message> filter = Filters.textPresent().and(Filters.textStartsWith("/"));

        assertTrue(filter.test(new Message(1, null, null, 1, "/ping", null, null, null)));
        assertFalse(filter.not().test(new Message(1, null, null, 1, "/ping", null, null, null)));
    }

    @Test
    void stateFiltersWorkWithContextAwareRouting() {
        InMemoryStateStorage storage = new InMemoryStateStorage();
        Router router = new Router();
        AtomicInteger noStateHits = new AtomicInteger();
        AtomicInteger inStateHits = new AtomicInteger();

        router.message(Filters.noState(), (ctx, msg) -> {
            noStateHits.incrementAndGet();
            ctx.state().set("form_name");
        });
        router.message(Filters.inStates("form_name", "form_lang"), (ctx, msg) -> inStateHits.incrementAndGet());

        UpdateContext first = new UpdateContext(messageUpdate(100L, "hello"), null, storage, "bot-1");
        UpdateContext second = new UpdateContext(messageUpdate(101L, "next"), null, storage, "bot-1");

        router.route(first);
        router.route(second);

        assertEquals(1, noStateHits.get());
        assertEquals(1, inStateHits.get());
    }

    private static Update messageUpdate(long updateId, String text) {
        Message message = new Message(
            (int) updateId,
            new User(42L, false, "A", null, "alice", "en", null, null, null),
            new Chat(100L, "private", null, null, "A", null, null),
            1,
            text,
            null,
            null,
            null
        );
        return new Update(updateId, message, null, null, null, null, null, null);
    }
}
