package ru.tardyon.botframework.telegram.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import ru.tardyon.botframework.telegram.api.capability.BotApiCapabilities;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportMode;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportProfile;
import ru.tardyon.botframework.telegram.diagnostics.BotApiRequestListener;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticsHooks;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.bot.TelegramBot;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.polling.LongPollingOptions;
import ru.tardyon.botframework.telegram.polling.LongPollingRunner;
import ru.tardyon.botframework.telegram.screen.ScreenEngine;
import ru.tardyon.botframework.telegram.screen.ScreenRegistry;
import ru.tardyon.botframework.telegram.screen.ScreenStateStorage;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;
import ru.tardyon.botframework.telegram.fsm.StateStorage;
import ru.tardyon.botframework.telegram.spring.boot.lifecycle.TelegramBotLifecycle;
import ru.tardyon.botframework.telegram.spring.boot.annotation.TelegramAnnotationHandlerRegistrar;
import ru.tardyon.botframework.telegram.spring.boot.annotation.TelegramScreenAnnotationRegistrar;
import ru.tardyon.botframework.telegram.spring.boot.service.TelegramBusinessOperations;
import ru.tardyon.botframework.telegram.spring.boot.service.TelegramMonetizationOperations;
import ru.tardyon.botframework.telegram.spring.boot.state.RedisStateStorage;
import ru.tardyon.botframework.telegram.spring.boot.widget.AnnotatedWidgetRegistry;
import ru.tardyon.botframework.telegram.spring.boot.widget.TelegramWidgetAnnotationRegistrar;
import ru.tardyon.botframework.telegram.spring.boot.webhook.TelegramWebhookController;
import ru.tardyon.botframework.telegram.webhook.WebhookUpdateProcessor;
import ru.tardyon.botframework.telegram.webapp.WebAppInitDataValidator;

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
                assertThat(context).hasSingleBean(TelegramScreenAnnotationRegistrar.class);
                assertThat(context).hasSingleBean(AnnotatedWidgetRegistry.class);
                assertThat(context).hasSingleBean(TelegramWidgetAnnotationRegistrar.class);
                assertThat(context).hasSingleBean(TelegramBot.class);
                assertThat(context).hasSingleBean(TelegramBotLifecycle.class);
                assertThat(context).hasSingleBean(WebhookUpdateProcessor.class);
                assertThat(context).hasSingleBean(WebAppInitDataValidator.class);
                assertThat(context).hasSingleBean(TelegramMonetizationOperations.class);
                assertThat(context).hasSingleBean(TelegramBusinessOperations.class);
                assertThat(context).hasSingleBean(ScreenStateStorage.class);
                assertThat(context).hasSingleBean(ScreenRegistry.class);
                assertThat(context).hasSingleBean(ScreenEngine.class);
                assertThat(context).hasSingleBean(BotApiTransportProfile.class);
                assertThat(context).hasSingleBean(DiagnosticsHooks.class);
                assertThat(context).hasSingleBean(BotApiCapabilities.class);
                assertThat(context).hasSingleBean(StateStorage.class);
                assertThat(context.getBean(StateStorage.class)).isInstanceOf(InMemoryStateStorage.class);
            });
    }

    @Test
    void createsRedisStateStorageWhenConfigured() {
        contextRunner
            .withUserConfiguration(RedisTemplateConfiguration.class)
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false",
                "telegram.bot.state.storage=redis",
                "telegram.bot.state.redis.key-prefix=test:fsm",
                "telegram.bot.state.redis.ttl-seconds=1800"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(StateStorage.class);
                assertThat(context.getBean(StateStorage.class)).isInstanceOf(RedisStateStorage.class);
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

    @Test
    void keepsManualRouterWiringWhenCustomRouterBeanProvided() {
        contextRunner
            .withUserConfiguration(CustomRouterConfiguration.class)
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false"
            )
            .run(context -> {
                Router router = context.getBean(Router.class);
                assertThat(router).isInstanceOf(CustomRouter.class);
            });
    }

    @Test
    void bindsTransportLocalProperties() {
        contextRunner
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false",
                "telegram.bot.transport.mode=local",
                "telegram.bot.transport.base-url=http://127.0.0.1:9081",
                "telegram.bot.transport.local-file-uri-upload-enabled=false"
            )
            .run(context -> {
                BotApiTransportProfile profile = context.getBean(BotApiTransportProfile.class);
                assertThat(profile.mode()).isEqualTo(BotApiTransportMode.LOCAL);
                assertThat(profile.baseUrl()).isEqualTo("http://127.0.0.1:9081");
                assertThat(profile.localFileUriUploadEnabled()).isFalse();
            });
    }

    @Test
    void disablesDiagnosticsHooksWhenConfigured() {
        contextRunner
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false",
                "telegram.bot.diagnostics.enabled=false"
            )
            .run(context -> {
                DiagnosticsHooks hooks = context.getBean(DiagnosticsHooks.class);
                assertThat(hooks).isSameAs(DiagnosticsHooks.noop());
            });
    }

    @Test
    void collectsDiagnosticsListenersFromContext() {
        contextRunner
            .withUserConfiguration(DiagnosticsConfiguration.class)
            .withPropertyValues(
                "telegram.bot.token=test-token",
                "telegram.bot.mode=polling",
                "telegram.bot.polling.enabled=false"
            )
            .run(context -> {
                DiagnosticsHooks hooks = context.getBean(DiagnosticsHooks.class);
                assertThat(hooks).isNotNull();
                assertThat(hooks).isNotSameAs(DiagnosticsHooks.noop());
            });
    }

    @Configuration(proxyBeanMethods = false)
    static class TestMiddlewareConfiguration {
        @Bean
        UpdateMiddleware testMiddleware() {
            return (context, chain) -> chain.proceed(context);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomRouterConfiguration {
        @Bean
        Router customRouter() {
            return new CustomRouter();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class DiagnosticsConfiguration {
        @Bean
        BotApiRequestListener testRequestListener() {
            return event -> {
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class RedisTemplateConfiguration {
        @Bean
        StringRedisTemplate stringRedisTemplate() {
            return Mockito.mock(StringRedisTemplate.class);
        }
    }

    static class CustomRouter extends Router {
    }
}
