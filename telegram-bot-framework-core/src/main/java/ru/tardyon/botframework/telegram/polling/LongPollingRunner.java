package ru.tardyon.botframework.telegram.polling;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticErrorEvent;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticsHooks;
import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;
import ru.tardyon.botframework.telegram.fsm.StateStorage;

public class LongPollingRunner {

    private final TelegramApiClient telegramApiClient;
    private final LongPollingOptions options;
    private final Consumer<Throwable> errorHandler;
    private final StateStorage stateStorage;
    private final String botId;
    private final DiagnosticsHooks diagnosticsHooks;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Integer nextOffset;
    private volatile Thread pollingThread;

    public LongPollingRunner(TelegramApiClient telegramApiClient) {
        this(telegramApiClient, LongPollingOptions.defaults());
    }

    public LongPollingRunner(TelegramApiClient telegramApiClient, LongPollingOptions options) {
        this(telegramApiClient, options, new InMemoryStateStorage(), botIdFromClient(telegramApiClient), throwable -> {
            System.err.println("LongPollingRunner error: " + throwable.getMessage());
            throwable.printStackTrace(System.err);
        }, DiagnosticsHooks.noop());
    }

    public LongPollingRunner(
        TelegramApiClient telegramApiClient,
        LongPollingOptions options,
        Consumer<Throwable> errorHandler
    ) {
        this(telegramApiClient, options, new InMemoryStateStorage(), botIdFromClient(telegramApiClient), errorHandler, DiagnosticsHooks.noop());
    }

    public LongPollingRunner(
        TelegramApiClient telegramApiClient,
        LongPollingOptions options,
        StateStorage stateStorage,
        String botId,
        Consumer<Throwable> errorHandler
    ) {
        this(telegramApiClient, options, stateStorage, botId, errorHandler, DiagnosticsHooks.noop());
    }

    public LongPollingRunner(
        TelegramApiClient telegramApiClient,
        LongPollingOptions options,
        StateStorage stateStorage,
        String botId,
        Consumer<Throwable> errorHandler,
        DiagnosticsHooks diagnosticsHooks
    ) {
        this.telegramApiClient = Objects.requireNonNull(telegramApiClient, "telegramApiClient must not be null");
        this.options = Objects.requireNonNull(options, "options must not be null");
        this.stateStorage = Objects.requireNonNull(stateStorage, "stateStorage must not be null");
        this.botId = requireBotId(botId);
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler must not be null");
        this.diagnosticsHooks = Objects.requireNonNull(diagnosticsHooks, "diagnosticsHooks must not be null");
    }

    public synchronized void start(Dispatcher dispatcher) {
        Objects.requireNonNull(dispatcher, "dispatcher must not be null");
        if (running.get()) {
            return;
        }

        running.set(true);
        Thread thread = new Thread(() -> runLoop(dispatcher), "telegram-long-polling-runner");
        pollingThread = thread;
        thread.start();
    }

    public synchronized void stop() {
        running.set(false);
        Thread thread = pollingThread;
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join(2_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        pollingThread = null;
    }

    public boolean isRunning() {
        return running.get();
    }

    Integer getNextOffset() {
        return nextOffset;
    }

    void pollOnce(Dispatcher dispatcher) {
        GetUpdatesRequest request = new GetUpdatesRequest(
            nextOffset,
            options.limit(),
            options.timeoutSeconds(),
            options.allowedUpdates()
        );

        List<Update> updates = telegramApiClient.getUpdates(request);
        if (updates == null || updates.isEmpty()) {
            return;
        }
        for (Update update : updates) {
            if (update == null) {
                continue;
            }

            try {
                UpdateContext context = new UpdateContext(update, telegramApiClient, stateStorage, botId);
                context.setAttribute(DiagnosticsHooks.UPDATE_SOURCE_ATTR, "POLLING");
                dispatcher.dispatch(context);
                advanceOffset(update);
            } catch (RuntimeException e) {
                throw new UpdateHandlingException(update, e);
            }
        }
    }

    private void runLoop(Dispatcher dispatcher) {
        while (running.get()) {
            try {
                pollOnce(dispatcher);
            } catch (UpdateHandlingException e) {
                diagnosticsHooks.onError(new DiagnosticErrorEvent(
                    null,
                    "polling",
                    "update-processing",
                    e.updateId(),
                    null,
                    e
                ));
                errorHandler.accept(e);
                running.set(false);
            } catch (RuntimeException e) {
                if (!running.get()) {
                    break;
                }
                diagnosticsHooks.onError(new DiagnosticErrorEvent(
                    null,
                    "polling",
                    "poll-loop",
                    null,
                    "getUpdates",
                    e
                ));
                errorHandler.accept(e);
                backoff();
            }
        }
    }

    private void backoff() {
        if (!running.get() || options.errorBackoffMillis() <= 0) {
            return;
        }
        try {
            Thread.sleep(options.errorBackoffMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running.set(false);
        }
    }

    private void advanceOffset(Update update) {
        Long updateId = update.updateId();
        if (updateId == null) {
            return;
        }
        int candidate = Math.toIntExact(updateId + 1);
        Integer current = nextOffset;
        if (current == null || candidate > current) {
            nextOffset = candidate;
        }
    }

    private static final class UpdateHandlingException extends RuntimeException {
        private final Long updateId;

        private UpdateHandlingException(Update update, Throwable cause) {
            super("Failed to process update with update_id=" + update.updateId(), cause);
            this.updateId = update.updateId();
        }

        private Long updateId() {
            return updateId;
        }
    }

    private static String botIdFromClient(TelegramApiClient telegramApiClient) {
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
