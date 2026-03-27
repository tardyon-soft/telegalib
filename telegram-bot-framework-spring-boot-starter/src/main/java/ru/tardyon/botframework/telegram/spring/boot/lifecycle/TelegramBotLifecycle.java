package ru.tardyon.botframework.telegram.spring.boot.lifecycle;

import org.springframework.context.SmartLifecycle;
import org.springframework.util.StringUtils;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.bot.TelegramBot;
import ru.tardyon.botframework.telegram.spring.boot.properties.TelegramBotFrameworkProperties;

public class TelegramBotLifecycle implements SmartLifecycle {

    private final TelegramBot telegramBot;
    private final TelegramApiClient telegramApiClient;
    private final TelegramBotFrameworkProperties properties;

    private volatile boolean running;

    public TelegramBotLifecycle(
        TelegramBot telegramBot,
        TelegramApiClient telegramApiClient,
        TelegramBotFrameworkProperties properties
    ) {
        this.telegramBot = telegramBot;
        this.telegramApiClient = telegramApiClient;
        this.properties = properties;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        if (properties.isPollingMode()) {
            telegramBot.startPolling();
            running = true;
            return;
        }
        if (properties.isWebhookMode()) {
            telegramBot.startWebhook();
            registerWebhookIfConfigured();
            running = true;
        }
    }

    private void registerWebhookIfConfigured() {
        String webhookUrl = properties.resolveWebhookUrl();
        if (!StringUtils.hasText(webhookUrl)) {
            return;
        }
        SetWebhookRequest request = new SetWebhookRequest(
            webhookUrl,
            null,
            null,
            properties.getPolling().getAllowedUpdates(),
            properties.getWebhook().getDropPendingUpdates(),
            properties.getWebhook().getSecretToken()
        );
        boolean success = telegramApiClient.setWebhook(request);
        if (!success) {
            throw new IllegalStateException("Telegram Bot API rejected setWebhook request");
        }
    }

    @Override
    public void stop() {
        if (running) {
            telegramBot.stop();
            running = false;
        }
    }

    @Override
    public void stop(Runnable callback) {
        try {
            stop();
        } finally {
            callback.run();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return properties.isPollingMode() || properties.isWebhookMode();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
