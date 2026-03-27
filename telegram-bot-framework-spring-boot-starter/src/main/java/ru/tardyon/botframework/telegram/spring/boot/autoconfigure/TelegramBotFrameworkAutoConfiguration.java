package ru.tardyon.botframework.telegram.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.api.DefaultTelegramApiClient;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.bot.DefaultTelegramBot;
import ru.tardyon.botframework.telegram.bot.TelegramBot;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.polling.LongPollingOptions;
import ru.tardyon.botframework.telegram.polling.LongPollingRunner;
import ru.tardyon.botframework.telegram.spring.boot.lifecycle.TelegramBotLifecycle;
import ru.tardyon.botframework.telegram.spring.boot.properties.TelegramBotFrameworkProperties;
import ru.tardyon.botframework.telegram.spring.boot.webhook.TelegramWebhookController;
import ru.tardyon.botframework.telegram.spring.boot.annotation.TelegramAnnotationHandlerRegistrar;
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
    public TelegramApiClient telegramApiClient(
        TelegramBotFrameworkProperties properties,
        HttpClient telegramHttpClient,
        ObjectMapper telegramObjectMapper
    ) {
        if (!StringUtils.hasText(properties.getToken())) {
            throw new IllegalStateException("telegram.bot.token must be configured");
        }
        return new DefaultTelegramApiClient(properties.getToken(), telegramHttpClient, telegramObjectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebAppInitDataValidator webAppInitDataValidator() {
        return new WebAppInitDataValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public LongPollingOptions longPollingOptions(TelegramBotFrameworkProperties properties) {
        TelegramBotFrameworkProperties.Polling polling = properties.getPolling();
        return new LongPollingOptions(polling.getTimeout(), polling.getLimit(), polling.getAllowedUpdates(), 1000L);
    }

    @Bean
    @ConditionalOnMissingBean
    public LongPollingRunner longPollingRunner(TelegramApiClient telegramApiClient, LongPollingOptions longPollingOptions) {
        return new LongPollingRunner(telegramApiClient, longPollingOptions);
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
        ObjectProvider<UpdateMiddleware> middlewareProvider
    ) {
        List<UpdateMiddleware> middlewares = middlewareProvider.orderedStream().toList();
        if (middlewares.isEmpty()) {
            return new DefaultDispatcher(telegramRouter);
        }
        return new DefaultDispatcher(telegramRouter, middlewares);
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
    public TelegramBot telegramBot(LongPollingRunner longPollingRunner, Dispatcher telegramDispatcher) {
        return new DefaultTelegramBot(longPollingRunner, telegramDispatcher);
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
        TelegramBotFrameworkProperties properties
    ) {
        return new DefaultWebhookUpdateProcessor(
            telegramObjectMapper,
            telegramDispatcher,
            telegramApiClient,
            properties.getWebhook().getSecretToken()
        );
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(prefix = "telegram.bot.webhook", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public TelegramWebhookController telegramWebhookController(WebhookUpdateProcessor webhookUpdateProcessor) {
        return new TelegramWebhookController(webhookUpdateProcessor);
    }
}
