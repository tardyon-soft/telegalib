package ru.tardyon.botframework.telegram.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;
import ru.tardyon.botframework.telegram.fsm.StateStorage;

public class DefaultWebhookUpdateProcessor implements WebhookUpdateProcessor {

    private final ObjectMapper objectMapper;
    private final Dispatcher dispatcher;
    private final TelegramApiClient telegramApiClient;
    private final String expectedSecretToken;
    private final StateStorage stateStorage;
    private final String botId;

    public DefaultWebhookUpdateProcessor(
        ObjectMapper objectMapper,
        Dispatcher dispatcher,
        TelegramApiClient telegramApiClient,
        String expectedSecretToken
    ) {
        this(
            objectMapper,
            dispatcher,
            telegramApiClient,
            expectedSecretToken,
            new InMemoryStateStorage(),
            botIdFromClient(telegramApiClient)
        );
    }

    public DefaultWebhookUpdateProcessor(
        ObjectMapper objectMapper,
        Dispatcher dispatcher,
        TelegramApiClient telegramApiClient,
        String expectedSecretToken,
        StateStorage stateStorage,
        String botId
    ) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher must not be null");
        this.telegramApiClient = telegramApiClient;
        this.expectedSecretToken = expectedSecretToken;
        this.stateStorage = Objects.requireNonNull(stateStorage, "stateStorage must not be null");
        this.botId = requireBotId(botId);
    }

    @Override
    public void process(String rawJsonBody, WebhookRequestMetadata metadata) {
        validateSecretToken(metadata);
        try {
            Update update = objectMapper.readValue(rawJsonBody, Update.class);
            dispatcher.dispatch(new UpdateContext(update, telegramApiClient, stateStorage, botId));
        } catch (WebhookSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse/process webhook update", e);
        }
    }

    private void validateSecretToken(WebhookRequestMetadata metadata) {
        if (expectedSecretToken == null || expectedSecretToken.isBlank()) {
            return;
        }
        String actualToken = metadata == null
            ? null
            : metadata.firstHeader(WebhookHeaders.TELEGRAM_SECRET_TOKEN_HEADER);
        if (!expectedSecretToken.equals(actualToken)) {
            throw new WebhookSecurityException("Invalid Telegram webhook secret token");
        }
    }

    private static String botIdFromClient(TelegramApiClient telegramApiClient) {
        if (telegramApiClient == null) {
            return "default-bot";
        }
        return telegramApiClient.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(telegramApiClient));
    }

    private static String requireBotId(String botId) {
        Objects.requireNonNull(botId, "botId must not be null");
        if (botId.isBlank()) {
            throw new IllegalArgumentException("botId must not be blank");
        }
        return botId;
    }
}
