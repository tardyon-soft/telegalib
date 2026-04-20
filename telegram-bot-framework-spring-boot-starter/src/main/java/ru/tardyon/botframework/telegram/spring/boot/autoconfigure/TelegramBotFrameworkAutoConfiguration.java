package ru.tardyon.botframework.telegram.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.api.DefaultTelegramApiClient;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.capability.BotApiCapabilities;
import ru.tardyon.botframework.telegram.api.capability.BotApiCapabilitiesResolver;
import ru.tardyon.botframework.telegram.api.capability.BotApiVersion;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportMode;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportProfile;
import ru.tardyon.botframework.telegram.bot.DefaultTelegramBot;
import ru.tardyon.botframework.telegram.bot.TelegramBot;
import ru.tardyon.botframework.telegram.diagnostics.BotApiRequestListener;
import ru.tardyon.botframework.telegram.diagnostics.BotApiResponseListener;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticsHooks;
import ru.tardyon.botframework.telegram.diagnostics.ErrorListener;
import ru.tardyon.botframework.telegram.diagnostics.SensitiveDataRedactor;
import ru.tardyon.botframework.telegram.diagnostics.UpdateProcessingListener;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;
import ru.tardyon.botframework.telegram.fsm.StateStorage;
import ru.tardyon.botframework.telegram.polling.LongPollingOptions;
import ru.tardyon.botframework.telegram.polling.LongPollingRunner;
import ru.tardyon.botframework.telegram.screen.InMemoryScreenStateStorage;
import ru.tardyon.botframework.telegram.screen.ScreenEngine;
import ru.tardyon.botframework.telegram.screen.ScreenMiddleware;
import ru.tardyon.botframework.telegram.screen.ScreenRegistry;
import ru.tardyon.botframework.telegram.screen.ScreenStateStorage;
import ru.tardyon.botframework.telegram.spring.boot.lifecycle.TelegramBotLifecycle;
import ru.tardyon.botframework.telegram.spring.boot.properties.TelegramBotFrameworkProperties;
import ru.tardyon.botframework.telegram.spring.boot.service.TelegramBusinessOperations;
import ru.tardyon.botframework.telegram.spring.boot.service.TelegramMonetizationOperations;
import ru.tardyon.botframework.telegram.spring.boot.state.RedisStateStorage;
import ru.tardyon.botframework.telegram.spring.boot.webhook.TelegramWebhookController;
import ru.tardyon.botframework.telegram.spring.boot.annotation.TelegramAnnotationHandlerRegistrar;
import ru.tardyon.botframework.telegram.spring.boot.annotation.TelegramScreenAnnotationRegistrar;
import ru.tardyon.botframework.telegram.spring.boot.widget.AnnotatedWidgetRegistry;
import ru.tardyon.botframework.telegram.spring.boot.widget.TelegramWidgetAnnotationRegistrar;
import ru.tardyon.botframework.telegram.webhook.DefaultWebhookUpdateProcessor;
import ru.tardyon.botframework.telegram.webhook.WebhookUpdateProcessor;
import ru.tardyon.botframework.telegram.webapp.WebAppInitDataValidator;

@AutoConfiguration
@EnableConfigurationProperties(TelegramBotFrameworkProperties.class)
public class TelegramBotFrameworkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HttpClient telegramHttpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper telegramObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public BotApiTransportProfile botApiTransportProfile(TelegramBotFrameworkProperties properties) {
        TelegramBotFrameworkProperties.Transport transport = properties.getTransport();
        BotApiTransportMode mode = transport.getMode() == null ? BotApiTransportMode.CLOUD : transport.getMode();
        String baseUrl = transport.resolveBaseUrl();
        if (mode == BotApiTransportMode.LOCAL) {
            return BotApiTransportProfile.local(baseUrl, transport.isLocalFileUriUploadEnabled());
        }
        return BotApiTransportProfile.cloud(baseUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public DiagnosticsHooks diagnosticsHooks(
        TelegramBotFrameworkProperties properties,
        ObjectProvider<BotApiRequestListener> requestListeners,
        ObjectProvider<BotApiResponseListener> responseListeners,
        ObjectProvider<UpdateProcessingListener> updateListeners,
        ObjectProvider<ErrorListener> errorListeners,
        ObjectProvider<SensitiveDataRedactor> sensitiveDataRedactor
    ) {
        if (!properties.getDiagnostics().isEnabled()) {
            return DiagnosticsHooks.noop();
        }
        DiagnosticsHooks.Builder builder = DiagnosticsHooks.builder();
        requestListeners.orderedStream().forEach(builder::addRequestListener);
        responseListeners.orderedStream().forEach(builder::addResponseListener);
        updateListeners.orderedStream().forEach(builder::addUpdateProcessingListener);
        errorListeners.orderedStream().forEach(builder::addErrorListener);
        SensitiveDataRedactor redactor = sensitiveDataRedactor.getIfAvailable();
        if (redactor != null) {
            builder.redactor(redactor);
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public BotApiCapabilities botApiCapabilities() {
        return BotApiCapabilitiesResolver.forDeclaredVersion(BotApiVersion.V9_5);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramApiClient telegramApiClient(
        TelegramBotFrameworkProperties properties,
        BotApiTransportProfile botApiTransportProfile,
        HttpClient telegramHttpClient,
        ObjectMapper telegramObjectMapper,
        DiagnosticsHooks diagnosticsHooks
    ) {
        if (!StringUtils.hasText(properties.getToken())) {
            throw new IllegalStateException("telegram.bot.token must be configured");
        }
        return new DefaultTelegramApiClient(
            properties.getToken(),
            botApiTransportProfile,
            telegramHttpClient,
            telegramObjectMapper,
            diagnosticsHooks
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public WebAppInitDataValidator webAppInitDataValidator() {
        return new WebAppInitDataValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        prefix = "telegram.bot.state",
        name = "storage",
        havingValue = "memory",
        matchIfMissing = true
    )
    public StateStorage telegramStateStorage() {
        return new InMemoryStateStorage();
    }

    @Bean
    @ConditionalOnMissingBean(StateStorage.class)
    @ConditionalOnClass(StringRedisTemplate.class)
    @ConditionalOnProperty(prefix = "telegram.bot.state", name = "storage", havingValue = "redis")
    public StateStorage redisTelegramStateStorage(
        StringRedisTemplate stringRedisTemplate,
        ObjectMapper telegramObjectMapper,
        TelegramBotFrameworkProperties properties
    ) {
        TelegramBotFrameworkProperties.Redis redis = properties.getState().getRedis();
        return new RedisStateStorage(
            stringRedisTemplate,
            telegramObjectMapper,
            redis.getKeyPrefix(),
            redis.getTtlSeconds()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ScreenStateStorage screenStateStorage() {
        return new InMemoryScreenStateStorage();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScreenRegistry screenRegistry() {
        return new ScreenRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScreenEngine screenEngine(ScreenRegistry screenRegistry, ScreenStateStorage screenStateStorage) {
        return new ScreenEngine(screenRegistry, screenStateStorage);
    }

    @Bean
    @ConditionalOnMissingBean(name = "screenMiddleware")
    public UpdateMiddleware screenMiddleware(ScreenEngine screenEngine) {
        return new ScreenMiddleware(screenEngine);
    }

    @Bean
    @ConditionalOnMissingBean
    public LongPollingOptions longPollingOptions(TelegramBotFrameworkProperties properties) {
        TelegramBotFrameworkProperties.Polling polling = properties.getPolling();
        return new LongPollingOptions(polling.getTimeout(), polling.getLimit(), polling.getAllowedUpdates(), 1000L);
    }

    @Bean
    @ConditionalOnMissingBean
    public LongPollingRunner longPollingRunner(
        TelegramApiClient telegramApiClient,
        LongPollingOptions longPollingOptions,
        StateStorage telegramStateStorage,
        DiagnosticsHooks diagnosticsHooks
    ) {
        return new LongPollingRunner(
            telegramApiClient,
            longPollingOptions,
            telegramStateStorage,
            buildBotId(telegramApiClient),
            defaultPollingErrorHandler(),
            diagnosticsHooks
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public Router telegramRouter() {
        return new Router();
    }

    @Bean
    @ConditionalOnMissingBean
    public Dispatcher telegramDispatcher(
        Router telegramRouter,
        ObjectProvider<UpdateMiddleware> middlewareProvider,
        DiagnosticsHooks diagnosticsHooks
    ) {
        List<UpdateMiddleware> middlewares = middlewareProvider.orderedStream().toList();
        if (middlewares.isEmpty()) {
            return new DefaultDispatcher(telegramRouter, List.of(), diagnosticsHooks);
        }
        return new DefaultDispatcher(telegramRouter, middlewares, diagnosticsHooks);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramAnnotationHandlerRegistrar telegramAnnotationHandlerRegistrar(
        Router telegramRouter,
        ListableBeanFactory beanFactory
    ) {
        return new TelegramAnnotationHandlerRegistrar(telegramRouter, beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramScreenAnnotationRegistrar telegramScreenAnnotationRegistrar(
        Router telegramRouter,
        ScreenRegistry screenRegistry,
        ScreenEngine screenEngine,
        ListableBeanFactory beanFactory
    ) {
        return new TelegramScreenAnnotationRegistrar(telegramRouter, screenRegistry, screenEngine, beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramBot telegramBot(LongPollingRunner longPollingRunner, Dispatcher telegramDispatcher) {
        return new DefaultTelegramBot(longPollingRunner, telegramDispatcher);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramMonetizationOperations telegramMonetizationOperations(TelegramApiClient telegramApiClient) {
        return new TelegramMonetizationOperations(telegramApiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramBusinessOperations telegramBusinessOperations(TelegramApiClient telegramApiClient) {
        return new TelegramBusinessOperations(telegramApiClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramBotLifecycle telegramBotLifecycle(
        TelegramBot telegramBot,
        TelegramApiClient telegramApiClient,
        TelegramBotFrameworkProperties properties
    ) {
        return new TelegramBotLifecycle(telegramBot, telegramApiClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebhookUpdateProcessor webhookUpdateProcessor(
        ObjectMapper telegramObjectMapper,
        Dispatcher telegramDispatcher,
        TelegramApiClient telegramApiClient,
        TelegramBotFrameworkProperties properties,
        StateStorage telegramStateStorage,
        DiagnosticsHooks diagnosticsHooks
    ) {
        return new DefaultWebhookUpdateProcessor(
            telegramObjectMapper,
            telegramDispatcher,
            telegramApiClient,
            properties.getWebhook().getSecretToken(),
            telegramStateStorage,
            buildBotId(telegramApiClient),
            diagnosticsHooks
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public AnnotatedWidgetRegistry annotatedWidgetRegistry() {
        return new AnnotatedWidgetRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public TelegramWidgetAnnotationRegistrar telegramWidgetAnnotationRegistrar(
        AnnotatedWidgetRegistry annotatedWidgetRegistry,
        ListableBeanFactory beanFactory
    ) {
        return new TelegramWidgetAnnotationRegistrar(annotatedWidgetRegistry, beanFactory);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(prefix = "telegram.bot.webhook", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public TelegramWebhookController telegramWebhookController(WebhookUpdateProcessor webhookUpdateProcessor) {
        return new TelegramWebhookController(webhookUpdateProcessor);
    }

    private static String buildBotId(TelegramApiClient telegramApiClient) {
        return telegramApiClient.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(telegramApiClient));
    }

    private static Consumer<Throwable> defaultPollingErrorHandler() {
        return throwable -> {
            System.err.println("LongPollingRunner error: " + throwable.getMessage());
            throwable.printStackTrace(System.err);
        };
    }
}
