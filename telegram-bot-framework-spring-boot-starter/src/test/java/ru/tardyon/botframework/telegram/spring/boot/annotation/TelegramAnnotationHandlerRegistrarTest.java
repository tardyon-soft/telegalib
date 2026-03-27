package ru.tardyon.botframework.telegram.spring.boot.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.ChosenInlineResult;
import ru.tardyon.botframework.telegram.api.model.InlineQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.spring.boot.autoconfigure.TelegramBotFrameworkAutoConfiguration;

class TelegramAnnotationHandlerRegistrarTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TelegramBotFrameworkAutoConfiguration.class))
        .withPropertyValues(
            "telegram.bot.token=test-token",
            "telegram.bot.mode=polling",
            "telegram.bot.polling.enabled=false"
        );

    @Test
    void registersAnnotatedHandlersIntoRouter() {
        contextRunner
            .withUserConfiguration(AnnotatedControllerConfiguration.class)
            .run(context -> {
                Router router = context.getBean(Router.class);
                TelegramApiClient apiClient = context.getBean(TelegramApiClient.class);
                TestAnnotatedController controller = context.getBean(TestAnnotatedController.class);

                router.route(new UpdateContext(updateWithMessage("/start"), apiClient));
                router.route(new UpdateContext(updateWithCallback("menu:main"), apiClient));
                router.route(new UpdateContext(updateWithInlineQuery("find"), apiClient));
                router.route(new UpdateContext(updateWithChosenInlineResult("find"), apiClient));

                UpdateContext stateful = new UpdateContext(updateWithMessage("name:John"), apiClient);
                stateful.state().set("form.awaiting_name");
                router.route(stateful);

                assertThat(controller.messageStartCalls.get()).isEqualTo(1);
                assertThat(controller.callbackCalls.get()).isEqualTo(1);
                assertThat(controller.inlineCalls.get()).isEqualTo(1);
                assertThat(controller.chosenCalls.get()).isEqualTo(1);
                assertThat(controller.stateCalls.get()).isEqualTo(1);
                assertThat(controller.lastMessageWrapper).isNotNull();
                assertThat(controller.lastCallbackWrapper).isNotNull();
            });
    }

    @Test
    void failsFastOnUnsupportedHandlerParameter() {
        contextRunner
            .withUserConfiguration(InvalidControllerConfiguration.class)
            .run(context -> {
                assertThat(context).hasFailed();
                assertThat(context.getStartupFailure())
                    .hasMessageContaining("Unsupported parameter type")
                    .hasMessageContaining("java.lang.String");
            });
    }

    private static Update updateWithMessage(String text) {
        Message message = new Message(
            1,
            new User(100L, false, "John", null, "john", "en", null, null, null),
            new Chat(200L, "private", null, null, "John", null, null),
            1_710_000_000,
            text,
            null,
            null,
            null
        );
        return new Update(1L, message, null, null, null, null, null, null);
    }

    private static Update updateWithCallback(String data) {
        CallbackQuery callbackQuery = new CallbackQuery(
            "cb-1",
            new User(100L, false, "John", null, "john", "en", null, null, null),
            null,
            null,
            "chat-instance",
            data,
            null
        );
        return new Update(2L, null, null, null, null, callbackQuery, null, null);
    }

    private static Update updateWithInlineQuery(String query) {
        InlineQuery inlineQuery = new InlineQuery(
            "iq-1",
            new User(100L, false, "John", null, "john", "en", null, null, null),
            null,
            query,
            "",
            "private"
        );
        return new Update(3L, null, null, null, null, null, inlineQuery, null);
    }

    private static Update updateWithChosenInlineResult(String query) {
        ChosenInlineResult chosenInlineResult = new ChosenInlineResult(
            "res-1",
            new User(100L, false, "John", null, "john", "en", null, null, null),
            null,
            "inline-message-id",
            query
        );
        return new Update(4L, null, null, null, null, null, null, chosenInlineResult);
    }

    @Configuration(proxyBeanMethods = false)
    static class AnnotatedControllerConfiguration {
        @Bean
        TestAnnotatedController testAnnotatedController() {
            return new TestAnnotatedController();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class InvalidControllerConfiguration {
        @Bean
        InvalidAnnotatedController invalidAnnotatedController() {
            return new InvalidAnnotatedController();
        }
    }

    @BotController
    static class TestAnnotatedController {
        private final AtomicInteger messageStartCalls = new AtomicInteger();
        private final AtomicInteger callbackCalls = new AtomicInteger();
        private final AtomicInteger inlineCalls = new AtomicInteger();
        private final AtomicInteger chosenCalls = new AtomicInteger();
        private final AtomicInteger stateCalls = new AtomicInteger();

        private TelegramMessage lastMessageWrapper;
        private TelegramCallbackQuery lastCallbackWrapper;

        @OnMessage(command = "start")
        public void onStart(UpdateContext context, Message message, TelegramMessage telegramMessage, Update update) {
            messageStartCalls.incrementAndGet();
            lastMessageWrapper = telegramMessage;
            assertThat(context).isNotNull();
            assertThat(message).isNotNull();
            assertThat(update).isNotNull();
        }

        @OnCallbackQuery(callbackPrefix = "menu:")
        public void onCallback(TelegramCallbackQuery callbackQueryWrapper, CallbackQuery callbackQuery) {
            callbackCalls.incrementAndGet();
            lastCallbackWrapper = callbackQueryWrapper;
            assertThat(callbackQuery).isNotNull();
        }

        @OnInlineQuery
        public void onInline(InlineQuery inlineQuery) {
            inlineCalls.incrementAndGet();
            assertThat(inlineQuery).isNotNull();
        }

        @OnChosenInlineResult
        public void onChosen(ChosenInlineResult chosenInlineResult) {
            chosenCalls.incrementAndGet();
            assertThat(chosenInlineResult).isNotNull();
        }

        @OnMessage(textStartsWith = "name:", state = "form.awaiting_name")
        public void onStatefulStep(UpdateContext context, Message message) {
            stateCalls.incrementAndGet();
            assertThat(context.state().getState()).isPresent();
            assertThat(message.text()).startsWith("name:");
        }
    }

    @BotController
    static class InvalidAnnotatedController {
        @OnMessage
        public void unsupported(String value) {
        }
    }
}
