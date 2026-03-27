package ru.tardyon.botframework.telegram.spring.boot.widget;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.screen.ScreenView;
import ru.tardyon.botframework.telegram.spring.boot.autoconfigure.TelegramBotFrameworkAutoConfiguration;

class TelegramWidgetAnnotationRegistrarTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TelegramBotFrameworkAutoConfiguration.class))
        .withPropertyValues(
            "telegram.bot.token=test-token",
            "telegram.bot.mode=polling",
            "telegram.bot.polling.enabled=false"
        );

    @Test
    void registersWidgetRenderAndActionHandlers() {
        contextRunner
            .withUserConfiguration(WidgetControllerConfiguration.class)
            .run(context -> {
                AnnotatedWidgetRegistry registry = context.getBean(AnnotatedWidgetRegistry.class);
                ScreenContext screenContext = dummyScreenContext();

                List<TestItem> items = List.of(new TestItem("1", "One"), new TestItem("2", "Two"));
                WidgetView view = registry.render("items_list", screenContext, items);
                assertThat(view.replyMarkup()).isNotNull();
                assertThat(view.lines()).contains("Список объектов");

                String callback = AnnotatedWidgetRegistry.encodeCallback("items_list", "open", "2");
                Optional<ScreenAction> action = registry.handleCallback(callback, screenContext);
                assertThat(action).isPresent();
                assertThat(action.get().kind()).isEqualTo(ScreenAction.Kind.PUSH);
                assertThat(action.get().targetScreenId()).isEqualTo("item_details");
                assertThat(screenContext.screenState().getData("selected_item_id")).contains("2");
            });
    }

    private ScreenContext dummyScreenContext() {
        User user = new User(100L, false, "John", null, "john", "en", null, null, null);
        Chat chat = new Chat(200L, "private", null, null, "John", null, null);
        Message message = new Message(1, user, chat, 1_710_000_000, "hi", null, null, null);
        ru.tardyon.botframework.telegram.api.model.Update update = new ru.tardyon.botframework.telegram.api.model.Update(
            1L,
            message,
            null,
            null,
            null,
            null,
            null,
            null
        );

        ru.tardyon.botframework.telegram.dispatcher.UpdateContext updateContext = new ru.tardyon.botframework.telegram.dispatcher.UpdateContext(
            update,
            null,
            new ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage(),
            "bot-test"
        );
        ru.tardyon.botframework.telegram.screen.ScreenKey key = new ru.tardyon.botframework.telegram.screen.ScreenKey("bot-test", 200L);
        ru.tardyon.botframework.telegram.screen.ScreenStateStorage storage = new ru.tardyon.botframework.telegram.screen.InMemoryScreenStateStorage();
        ru.tardyon.botframework.telegram.screen.ScreenStack stack = storage.getOrCreate(key);
        stack.push("items");
        ru.tardyon.botframework.telegram.screen.ScreenStateContext screenStateContext =
            new ru.tardyon.botframework.telegram.screen.ScreenStateContext(storage, key);

        return new ScreenContext(updateContext, screenStateContext, new ru.tardyon.botframework.telegram.screen.ScreenNavigator() {
            @Override
            public void push(String screenId) {
            }

            @Override
            public void replace(String screenId) {
            }

            @Override
            public boolean back() {
                return false;
            }

            @Override
            public void clear() {
            }

            @Override
            public java.util.Optional<String> currentScreenId() {
                return java.util.Optional.of("items");
            }

            @Override
            public void renderCurrent() {
            }
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class WidgetControllerConfiguration {
        @Bean
        DemoWidgets demoWidgets() {
            return new DemoWidgets();
        }
    }

    record TestItem(String id, String title) {
    }

    @WidgetController
    static class DemoWidgets {

        @Widget(id = "items_list")
        public WidgetView itemListWidget(List<TestItem> items) {
            return WidgetView.builder()
                .line("Список объектов")
                .replyMarkup(WidgetButtons.objectList("items_list", "open", items, TestItem::title, TestItem::id))
                .build();
        }

        @OnWidgetAction(widget = "items_list", action = "open")
        public ScreenAction onOpen(ScreenContext context, String payload, TelegramCallbackQuery callbackQuery, CallbackQuery raw) {
            context.screenState().putData("selected_item_id", payload);
            if (callbackQuery != null) {
                callbackQuery.answer();
            }
            return ScreenAction.push("item_details");
        }
    }
}
