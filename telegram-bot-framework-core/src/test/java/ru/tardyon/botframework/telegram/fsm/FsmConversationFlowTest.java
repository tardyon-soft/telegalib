package ru.tardyon.botframework.telegram.fsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

class FsmConversationFlowTest {

    @Test
    void runsThreeStepConversationAndClearsState() {
        InMemoryStateStorage storage = new InMemoryStateStorage();
        Router router = new Router();
        AtomicReference<String> finalResult = new AtomicReference<>();

        router.message(Filters.command("startform"), (ctx, msg) -> {
            ctx.state().set("await_name");
            ctx.state().data().clear();
        });

        router.message(Filters.stateEquals("await_name"), (ctx, msg) -> {
            if (msg.text() == null || msg.text().startsWith("/")) {
                return;
            }
            ctx.state().data().put("name", msg.text());
            ctx.state().set("await_language");
        });

        router.message(Filters.stateEquals("await_language"), (ctx, msg) -> {
            if (msg.text() == null || msg.text().startsWith("/")) {
                return;
            }
            String name = ctx.state().data().get("name", String.class).orElse("unknown");
            finalResult.set(name + ":" + msg.text());
            ctx.state().clear();
        });

        DefaultDispatcher dispatcher = new DefaultDispatcher(router);

        dispatcher.dispatch(new UpdateContext(messageUpdate(1L, "/startform"), null, storage, "bot-1"));
        dispatcher.dispatch(new UpdateContext(messageUpdate(2L, "Alice"), null, storage, "bot-1"));
        dispatcher.dispatch(new UpdateContext(messageUpdate(3L, "Java"), null, storage, "bot-1"));

        StateKey key = new StateKey("bot-1", 100L, 42L);
        assertEquals("Alice:Java", finalResult.get());
        assertTrue(storage.getState(key).isEmpty());
        assertTrue(storage.getData(key).isEmpty());
    }

    @Test
    void resolvesStateKeyForCallbackWithMessage() {
        InMemoryStateStorage storage = new InMemoryStateStorage();
        Message callbackMessage = new Message(
            11,
            null,
            new Chat(500L, "private", null, null, null, null, null),
            1,
            "button",
            null,
            null,
            null
        );
        CallbackQuery callbackQuery = new CallbackQuery(
            "cb1",
            new User(77L, false, "A", null, "a", "en", null, null, null),
            callbackMessage,
            null,
            "ci",
            "menu:1",
            null
        );

        UpdateContext context = new UpdateContext(
            new Update(10L, null, null, null, null, callbackQuery, null, null),
            null,
            storage,
            "bot-2"
        );

        context.state().set("callback_state");
        StateKey key = new StateKey("bot-2", 500L, 77L);
        assertEquals("callback_state", storage.getState(key).orElseThrow().value());
    }

    private static Update messageUpdate(long updateId, String text) {
        Message message = new Message(
            (int) updateId,
            new User(42L, false, "Alice", null, "alice", "en", null, null, null),
            new Chat(100L, "private", null, null, "Alice", null, null),
            1,
            text,
            null,
            null,
            null
        );
        return new Update(updateId, message, null, null, null, null, null, null);
    }
}
