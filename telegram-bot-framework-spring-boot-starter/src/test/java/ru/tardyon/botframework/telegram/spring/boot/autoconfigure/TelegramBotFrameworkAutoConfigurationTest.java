package ru.tardyon.botframework.telegram.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.bot.TelegramBot;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.polling.LongPollingOptions;
import ru.tardyon.botframework.telegram.polling.LongPollingRunner;
import ru.tardyon.botframework.telegram.spring.boot.lifecycle.TelegramBotLifecycle;
import ru.tardyon.botframework.telegram.spring.boot.annotation.TelegramAnnotationHandlerRegistrar;
import ru.tardyon.botframework.telegram.spring.boot.webhook.TelegramWebhookController;
import ru.tardyon.botframework.telegram.webhook.WebhookUpdateProcessor;

class TelegramBotFrameworkAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TelegramBotFrameworkAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TelegramBotFrameworkAutoConfiguration.class));

    @Test
    void createsCoreRuntimeBeans() {
        contextRunner
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(TelegramApiClient.class);
                assertThat(context).hasSingleBean(LongPollingOptions.class);
                assertThat(context).hasSingleBean(LongPollingRunner.class);
                assertThat(context).hasSingleBean(Router.class);
                assertThat(context).hasSingleBean(Dispatcher.class);
                assertThat(context).hasSingleBean(TelegramAnnotationHandlerRegistrar.class);
                assertThat(context).hasSingleBean(TelegramBot.class);
                assertThat(context).hasSingleBean(TelegramBotLifecycle.class);
                assertThat(context).hasSingleBean(WebhookUpdateProcessor.class);
            });
    }

    @Test
    void bindsPollingPropertiesIntoLongPollingOptions() {
        contextRunner
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false",
                "telegram.bot.polling.timeout=15",
                "telegram.bot.polling.limit=42",
                "telegram.bot.polling.allowed-updates[0]=message",
                "telegram.bot.polling.allowed-updates[1]=callback_query"
            )
            .run(context -> {
                LongPollingOptions options = context.getBean(LongPollingOptions.class);
                assertThat(options.timeoutSeconds()).isEqualTo(15);
                assertThat(options.limit()).isEqualTo(42);
                assertThat(options.allowedUpdates()).containsExactly("message", "callback_query");
            });
    }

    @Test
    void wiresCustomMiddlewaresIntoDispatcher() {
        contextRunner
            .withUserConfiguration(TestMiddlewareConfiguration.class)
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false"
            )
            .run(context -> {
                Dispatcher dispatcher = context.getBean(Dispatcher.class);
                assertThat(dispatcher).isInstanceOf(DefaultDispatcher.class);
            });
    }

    @Test
    void createsWebhookControllerOnlyWhenWebhookEnabledInWebApp() {
        webContextRunner
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=webhook",
                "telegram.bot.webhook.enabled=true",
                "telegram.bot.webhook.path=/telegram/webhook"
            )
            .run(context -> assertThat(context).hasSingleBean(TelegramWebhookController.class));
    }

    @Test
    void doesNotCreateWebhookControllerWhenWebhookDisabled() {
        webContextRunner
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=webhook",
                "telegram.bot.webhook.enabled=false"
            )
            .run(context -> assertThat(context).doesNotHaveBean(TelegramWebhookController.class));
    }

    @Configuration(proxyBeanMethods = false)
    static class TestMiddlewareConfiguration {
        @Bean
        UpdateMiddleware testMiddleware() {
            return (context, chain) -> chain.proceed(context);
        }
    }
}
