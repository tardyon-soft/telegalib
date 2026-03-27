package ru.tardyon.botframework.telegram.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;
import ru.tardyon.botframework.telegram.fsm.StateStorage;

class ScreenEngineTest {

    @Test
    void screenStackSupportsPushAndBack() {
        InMemoryScreenStateStorage screenStorage = new InMemoryScreenStateStorage();
        ScreenRegistry registry = new ScreenRegistry();
        List<String> rendered = new ArrayList<>();
        ScreenViewRenderer renderer = (ctx, screenState, chatId, view) ->
            rendered.add(screenState.currentScreenId().orElse("none"));

        registry.register(new Screen() {
            @Override
            public String id() {
                return "home";
            }

            @Override
            public ScreenView render(ScreenContext context) {
                return ScreenView.builder().text("Home").build();
            }

            @Override
            public ScreenAction onCallbackQuery(ScreenContext context, CallbackQuery callbackQuery) {
                String data = callbackQuery.data();
                if ("open_settings".equals(data)) {
                    return ScreenAction.push("settings");
                }
                if (ScreenCallbackData.back().equals(data)) {
                    return ScreenAction.back();
                }
                return ScreenAction.unhandled();
            }
        });

        registry.register(new Screen() {
            @Override
            public String id() {
                return "settings";
            }

            @Override
            public ScreenView render(ScreenContext context) {
                return ScreenView.builder().text("Settings").build();
            }

            @Override
            public ScreenAction onCallbackQuery(ScreenContext context, CallbackQuery callbackQuery) {
                if (ScreenCallbackData.back().equals(callbackQuery.data())) {
                    return ScreenAction.back();
                }
                return ScreenAction.unhandled();
            }
        });

        ScreenEngine engine = new ScreenEngine(registry, screenStorage, renderer);
        StateStorage userStateStorage = new InMemoryStateStorage();

        UpdateContext startContext = new UpdateContext(messageUpdate(1L, "screen_start"), null, userStateStorage, "bot-test");
        engine.start(startContext, "home");

        UpdateContext pushContext = new UpdateContext(callbackUpdate(2L, "open_settings"), null, userStateStorage, "bot-test");
        assertTrue(engine.handle(pushContext));

        UpdateContext backContext = new UpdateContext(callbackUpdate(3L, ScreenCallbackData.back()), null, userStateStorage, "bot-test");
        assertTrue(engine.handle(backContext));

        ScreenStack stack = screenStorage.find(new ScreenKey("bot-test", 1001L)).orElse(null);
        assertNotNull(stack);
        assertEquals(1, stack.size());
        assertEquals("home", stack.current().orElseThrow().screenId());
        assertEquals(List.of("home", "settings", "home"), rendered);
    }

    @Test
    void screenStateIsSeparatedFromUserState() {
        InMemoryScreenStateStorage screenStorage = new InMemoryScreenStateStorage();
        ScreenRegistry registry = new ScreenRegistry();
        ScreenViewRenderer renderer = (ctx, screenState, chatId, view) -> {
        };

        registry.register(new Screen() {
            @Override
            public String id() {
                return "form";
            }

            @Override
            public ScreenView render(ScreenContext context) {
                return ScreenView.builder().text("Form").build();
            }

            @Override
            public ScreenAction onMessage(ScreenContext context, Message message) {
                context.screenState().putData("screen_key", "screen_value");
                context.userState().putData("user_key", "user_value");
                return ScreenAction.handled();
            }
        });

        ScreenEngine engine = new ScreenEngine(registry, screenStorage, renderer);
        StateStorage userStateStorage = new InMemoryStateStorage();

        UpdateContext startContext = new UpdateContext(messageUpdate(10L, "start"), null, userStateStorage, "bot-test");
        engine.start(startContext, "form");
        engine.handle(startContext);

        ScreenStack stack = screenStorage.find(new ScreenKey("bot-test", 1001L)).orElseThrow();
        assertEquals("screen_value", stack.current().orElseThrow().getData("screen_key").orElse(null));
        assertEquals("user_value", startContext.state().getData("user_key").orElse(null));

        engine.handle(new UpdateContext(callbackUpdate(11L, ScreenCallbackData.back()), null, userStateStorage, "bot-test"));
        assertEquals("user_value", startContext.state().getData("user_key").orElse(null));
    }

    @Test
    void middlewareDelegatesWhenNoActiveScreen() {
        ScreenEngine engine = new ScreenEngine(new ScreenRegistry(), new InMemoryScreenStateStorage(), (ctx, screenState, chatId, view) -> {
        });
        ScreenMiddleware middleware = new ScreenMiddleware(engine);
        AtomicBoolean proceeded = new AtomicBoolean(false);

        UpdateContext context = new UpdateContext(messageUpdate(20L, "ping"), null, new InMemoryStateStorage(), "bot-test");
        middleware.handle(context, updateContext -> proceeded.set(true));

        assertTrue(proceeded.get());
        assertFalse(engine.handle(new UpdateContext(messageUpdate(21L, "pong"), null, new InMemoryStateStorage(), "bot-test")));
    }

    private static Update messageUpdate(long updateId, String text) {
        User user = new User(5001L, false, "Sergej", null, "sergej", null, null, null, null);
        Chat chat = new Chat(1001L, "private", null, null, "Sergej", null, null);
        Message message = new Message(3001, user, chat, 1, text, null, null, null);
        return new Update(updateId, message, null, null, null, null, null, null);
    }

    private static Update callbackUpdate(long updateId, String callbackData) {
        User user = new User(5001L, false, "Sergej", null, "sergej", null, null, null, null);
        Chat chat = new Chat(1001L, "private", null, null, "Sergej", null, null);
        Message callbackMessage = new Message(3002, user, chat, 1, "screen", null, null, null);
        CallbackQuery callbackQuery = new CallbackQuery("cb-" + updateId, user, callbackMessage, null, "chat-instance", callbackData, null);
        return new Update(updateId, null, null, null, null, callbackQuery, null, null);
    }
}
