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
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.business.BusinessMessagesDeleted;
import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingAddress;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingQuery;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppData;
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
                router.route(new UpdateContext(updateWithShippingQuery("payload:1"), apiClient));
                router.route(new UpdateContext(updateWithPreCheckoutQuery("payload:1"), apiClient));
                router.route(new UpdateContext(updateWithBusinessConnection(), apiClient));
                router.route(new UpdateContext(updateWithBusinessMessage("biz"), apiClient));
                router.route(new UpdateContext(updateWithEditedBusinessMessage("biz-edited"), apiClient));
                router.route(new UpdateContext(updateWithDeletedBusinessMessages(), apiClient));
                router.route(new UpdateContext(updateWithWebAppDataMessage(), apiClient));

                UpdateContext stateful = new UpdateContext(updateWithMessage("name:John"), apiClient);
                stateful.state().set("form.awaiting_name");
                router.route(stateful);

                assertThat(controller.messageStartCalls.get()).isEqualTo(1);
                assertThat(controller.callbackCalls.get()).isEqualTo(1);
                assertThat(controller.inlineCalls.get()).isEqualTo(1);
                assertThat(controller.chosenCalls.get()).isEqualTo(1);
                assertThat(controller.stateCalls.get()).isEqualTo(1);
                assertThat(controller.shippingCalls.get()).isEqualTo(1);
                assertThat(controller.preCheckoutCalls.get()).isEqualTo(1);
                assertThat(controller.businessConnectionCalls.get()).isEqualTo(1);
                assertThat(controller.businessMessageCalls.get()).isEqualTo(1);
                assertThat(controller.editedBusinessMessageCalls.get()).isEqualTo(1);
                assertThat(controller.deletedBusinessMessagesCalls.get()).isEqualTo(1);
                assertThat(controller.webAppDataCalls.get()).isEqualTo(1);
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

    private static Update updateWithWebAppDataMessage() {
        Message message = new Message(
            null,
            11,
            new User(100L, false, "John", null, "john", "en", null, null, null),
            null,
            new Chat(200L, "private", null, null, "John", null, null),
            1_710_000_001,
            null,
            null,
            null,
            null,
            null,
            null,
            new WebAppData("{\"ok\":true}", "Open App")
        );
        return new Update(11L, message, null, null, null, null, null, null, null, null, null, null, null, null);
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

    private static Update updateWithShippingQuery(String payload) {
        ShippingQuery shippingQuery = new ShippingQuery(
            "sq-1",
            new User(100L, false, "John", null, "john", "en", null, null, null),
            payload,
            new ShippingAddress("US", "CA", "LA", "Line1", "Line2", "90001")
        );
        return new Update(5L, null, null, null, null, null, shippingQuery, null, null, null, null, null, null, null);
    }

    private static Update updateWithPreCheckoutQuery(String payload) {
        PreCheckoutQuery preCheckoutQuery = new PreCheckoutQuery(
            "pcq-1",
            new User(100L, false, "John", null, "john", "en", null, null, null),
            "XTR",
            100,
            payload,
            null,
            null
        );
        return new Update(6L, null, null, null, null, null, null, preCheckoutQuery, null, null, null, null, null, null);
    }

    private static Update updateWithBusinessConnection() {
        BusinessConnection businessConnection = new BusinessConnection(
            "bc-1",
            new User(100L, false, "John", null, "john", "en", null, null, null),
            9001L,
            1_710_000_002,
            null,
            true
        );
        return new Update(7L, null, null, null, null, null, null, null, businessConnection, null, null, null, null, null);
    }

    private static Update updateWithBusinessMessage(String text) {
        Message message = new Message(
            "bc-1",
            12,
            new User(100L, false, "John", null, "john", "en", null, null, null),
            null,
            new Chat(200L, "private", null, null, "John", null, null),
            1_710_000_003,
            text,
            null,
            null,
            null
        );
        return new Update(8L, null, null, null, null, null, null, null, null, message, null, null, null, null);
    }

    private static Update updateWithEditedBusinessMessage(String text) {
        Message message = new Message(
            "bc-1",
            13,
            new User(100L, false, "John", null, "john", "en", null, null, null),
            null,
            new Chat(200L, "private", null, null, "John", null, null),
            1_710_000_004,
            text,
            null,
            null,
            null
        );
        return new Update(9L, null, null, null, null, null, null, null, null, null, message, null, null, null);
    }

    private static Update updateWithDeletedBusinessMessages() {
        BusinessMessagesDeleted deleted = new BusinessMessagesDeleted(
            "bc-1",
            new Chat(200L, "private", null, null, "John", null, null),
            java.util.List.of(12, 13)
        );
        return new Update(10L, null, null, null, null, null, null, null, null, null, null, deleted, null, null);
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
        private final AtomicInteger shippingCalls = new AtomicInteger();
        private final AtomicInteger preCheckoutCalls = new AtomicInteger();
        private final AtomicInteger businessConnectionCalls = new AtomicInteger();
        private final AtomicInteger businessMessageCalls = new AtomicInteger();
        private final AtomicInteger editedBusinessMessageCalls = new AtomicInteger();
        private final AtomicInteger deletedBusinessMessagesCalls = new AtomicInteger();
        private final AtomicInteger webAppDataCalls = new AtomicInteger();

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

        @OnShippingQuery(invoicePayloadEquals = "payload:1")
        public void onShipping(ShippingQuery shippingQuery) {
            shippingCalls.incrementAndGet();
            assertThat(shippingQuery.invoicePayload()).isEqualTo("payload:1");
        }

        @OnPreCheckoutQuery(payloadEquals = "payload:1")
        public void onPreCheckout(PreCheckoutQuery preCheckoutQuery) {
            preCheckoutCalls.incrementAndGet();
            assertThat(preCheckoutQuery.invoicePayload()).isEqualTo("payload:1");
        }

        @OnBusinessConnection
        public void onBusinessConnection(BusinessConnection businessConnection) {
            businessConnectionCalls.incrementAndGet();
            assertThat(businessConnection.id()).isEqualTo("bc-1");
        }

        @OnBusinessMessage(textEquals = "biz")
        public void onBusinessMessage(Message message) {
            businessMessageCalls.incrementAndGet();
            assertThat(message.businessConnectionId()).isEqualTo("bc-1");
        }

        @OnEditedBusinessMessage(textStartsWith = "biz")
        public void onEditedBusinessMessage(Message message) {
            editedBusinessMessageCalls.incrementAndGet();
            assertThat(message.text()).startsWith("biz");
        }

        @OnDeletedBusinessMessages
        public void onDeletedBusinessMessages(BusinessMessagesDeleted deleted) {
            deletedBusinessMessagesCalls.incrementAndGet();
            assertThat(deleted.messageIds()).containsExactly(12, 13);
        }

        @OnMessage(webAppDataPresent = true)
        public void onWebAppData(WebAppData webAppData, UpdateContext context) {
            webAppDataCalls.incrementAndGet();
            assertThat(webAppData).isNotNull();
            assertThat(webAppData.buttonText()).isEqualTo("Open App");
            assertThat(context).isNotNull();
        }
    }

    @BotController
    static class InvalidAnnotatedController {
        @OnMessage
        public void unsupported(String value) {
        }
    }
}
