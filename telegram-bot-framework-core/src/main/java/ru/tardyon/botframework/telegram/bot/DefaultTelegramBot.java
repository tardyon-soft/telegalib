package ru.tardyon.botframework.telegram.bot;

import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;
import ru.tardyon.botframework.telegram.polling.LongPollingRunner;

public class DefaultTelegramBot implements TelegramBot {

    private enum Mode {
        STOPPED,
        POLLING,
        WEBHOOK
    }

    private final LongPollingRunner longPollingRunner;
    private final Dispatcher dispatcher;
    private Mode mode = Mode.STOPPED;

    public DefaultTelegramBot(LongPollingRunner longPollingRunner, Dispatcher dispatcher) {
        this.longPollingRunner = longPollingRunner;
        this.dispatcher = dispatcher;
    }

    @Override
    public synchronized void startPolling() {
        if (mode == Mode.WEBHOOK) {
            throw new IllegalStateException("Bot is in webhook mode; stop it before starting polling");
        }
        longPollingRunner.start(dispatcher);
        mode = Mode.POLLING;
    }

    @Override
    public synchronized void startWebhook() {
        if (mode == Mode.POLLING) {
            throw new IllegalStateException("Bot is in polling mode; stop it before starting webhook");
        }
        mode = Mode.WEBHOOK;
    }

    @Override
    public synchronized void stop() {
        if (mode == Mode.POLLING) {
            longPollingRunner.stop();
        }
        mode = Mode.STOPPED;
    }
}
