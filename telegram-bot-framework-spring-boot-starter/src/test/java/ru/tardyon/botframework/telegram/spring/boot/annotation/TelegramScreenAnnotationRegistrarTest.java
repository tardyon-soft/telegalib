package ru.tardyon.botframework.telegram.spring.boot.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenCallbackData;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.screen.ScreenEngine;
import ru.tardyon.botframework.telegram.screen.ScreenStateStorage;
import ru.tardyon.botframework.telegram.screen.ScreenView;
import ru.tardyon.botframework.telegram.screen.ScreenViewRenderer;
import ru.tardyon.botframework.telegram.spring.boot.autoconfigure.TelegramBotFrameworkAutoConfiguration;

class TelegramScreenAnnotationRegistrarTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TelegramBotFrameworkAutoConfiguration.class))
        .withPropertyValues(
            "telegram.bot.token=test-token",
            "telegram.bot.mode=polling",
            "telegram.bot.polling.enabled=false"
        );

    @Test
    void registersScreenAnnotationsAndRoutesStartCommand() {
        contextRunner
            .withUserConfiguration(ScreenTestConfiguration.class)
            .run(context -> {
                Router router = context.getBean(Router.class);
                ScreenEngine screenEngine = context.getBean(ScreenEngine.class);
                ScreenStateStorage screenStateStorage = context.getBean(ScreenStateStorage.class);
                InMemoryStateStorage userStateStorage = new InMemoryStateStorage();

                UpdateContext startContext = new UpdateContext(messageUpdate(1L, "/screen_start"), null, userStateStorage, "bot-test");
                router.route(startContext);

                String currentAfterStart = screenStateStorage.find(new ru.tardyon.botframework.telegram.screen.ScreenKey("bot-test", 200L))
                    .flatMap(stack -> stack.current().map(ru.tardyon.botframework.telegram.screen.ScreenFrame::screenId))
                    .orElse(null);
                assertThat(currentAfterStart).isEqualTo("home");

                UpdateContext settingsContext = new UpdateContext(messageUpdate(2L, "to_settings"), null, userStateStorage, "bot-test");
                assertThat(screenEngine.handle(settingsContext)).isTrue();

                String currentAfterPush = screenStateStorage.find(new ru.tardyon.botframework.telegram.screen.ScreenKey("bot-test", 200L))
                    .flatMap(stack -> stack.current().map(ru.tardyon.botframework.telegram.screen.ScreenFrame::screenId))
                    .orElse(null);
                assertThat(currentAfterPush).isEqualTo("settings");

                UpdateContext backContext = new UpdateContext(callbackUpdate(3L, ScreenCallbackData.back()), null, userStateStorage, "bot-test");
                assertThat(screenEngine.handle(backContext)).isTrue();

                String currentAfterBack = screenStateStorage.find(new ru.tardyon.botframework.telegram.screen.ScreenKey("bot-test", 200L))
                    .flatMap(stack -> stack.current().map(ru.tardyon.botframework.telegram.screen.ScreenFrame::screenId))
                    .orElse(null);
                assertThat(currentAfterBack).isEqualTo("home");
            });
    }

    @Test
    void failsFastWhenScreenViewMethodHasInvalidReturnType() {
        contextRunner
            .withUserConfiguration(InvalidScreenConfiguration.class)
            .run(context -> {
                assertThat(context).hasFailed();
                assertThat(context.getStartupFailure())
                    .hasMessageContaining("Screen view method must return ScreenView");
            });
    }

    private static Update messageUpdate(long updateId, String text) {
        Message message = new Message(
            10,
            new User(100L, false, "John", null, "john", "en", null, null, null),
            new Chat(200L, "private", null, null, "John", null, null),
            1_710_000_000,
            text,
            null,
            null,
            null
        );
        return new Update(updateId, message, null, null, null, null, null, null);
    }

    private static Update callbackUpdate(long updateId, String callbackData) {
        Message message = new Message(
            11,
            new User(100L, false, "John", null, "john", "en", null, null, null),
            new Chat(200L, "private", null, null, "John", null, null),
            1_710_000_000,
            "screen",
            null,
            null,
            null
        );
        CallbackQuery callbackQuery = new CallbackQuery(
            "cb-" + updateId,
            new User(100L, false, "John", null, "john", "en", null, null, null),
            message,
            null,
            "chat-instance",
            callbackData,
            null
        );
        return new Update(updateId, null, null, null, null, callbackQuery, null, null);
    }

    @Configuration(proxyBeanMethods = false)
    static class ScreenTestConfiguration {
        @Bean
        ScreenViewRenderer testScreenViewRenderer() {
            return (updateContext, screenStateContext, chatId, view) -> {
            };
        }

        @Bean
        ScreenEngine screenEngine(ru.tardyon.botframework.telegram.screen.ScreenRegistry screenRegistry, ScreenStateStorage screenStateStorage) {
            return new ScreenEngine(screenRegistry, screenStateStorage, testScreenViewRenderer());
        }

        @Bean
        TestScreenController testScreenController() {
            return new TestScreenController();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class InvalidScreenConfiguration {
        @Bean
        InvalidScreenController invalidScreenController() {
            return new InvalidScreenController();
        }
    }

    @ScreenController
    static class TestScreenController {

        @Screen(id = "home", startCommand = "screen_start")
        public ScreenView home(ScreenContext context) {
            return ScreenView.builder().text("home").build();
        }

        @Screen(id = "settings")
        public ScreenView settings(ScreenContext context) {
            return ScreenView.builder().text("settings").build();
        }

        @OnScreenMessage(screen = "home", textEquals = "to_settings")
        public ScreenAction toSettings(Message message) {
            return ScreenAction.push("settings");
        }

        @OnScreenCallback(screen = "settings", callbackEquals = "screen:nav:back")
        public ScreenAction back(CallbackQuery callbackQuery) {
            return ScreenAction.back();
        }
    }

    @ScreenController
    static class InvalidScreenController {
        @Screen(id = "bad")
        public String badView() {
            return "bad";
        }
    }
}
