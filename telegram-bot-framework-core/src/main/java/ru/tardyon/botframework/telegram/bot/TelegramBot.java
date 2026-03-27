package ru.tardyon.botframework.telegram.bot;

public interface TelegramBot {

    void startPolling();

    void startWebhook();

    default void start() {
        startPolling();
    }

    void stop();
}
