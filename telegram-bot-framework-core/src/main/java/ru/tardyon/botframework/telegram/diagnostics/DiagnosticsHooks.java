package ru.tardyon.botframework.telegram.diagnostics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Listener registry and dispatcher for diagnostics callbacks.
 */
public final class DiagnosticsHooks {

    public static final String CORRELATION_ID_ATTR = "diagnostics.correlation_id";
    public static final String UPDATE_SOURCE_ATTR = "diagnostics.update_source";

    private static final DiagnosticsHooks NOOP = new Builder().build();

    private final List<BotApiRequestListener> requestListeners;
    private final List<BotApiResponseListener> responseListeners;
    private final List<UpdateProcessingListener> updateListeners;
    private final List<ErrorListener> errorListeners;
    private final SensitiveDataRedactor redactor;

    private DiagnosticsHooks(Builder builder) {
        this.requestListeners = List.copyOf(builder.requestListeners);
        this.responseListeners = List.copyOf(builder.responseListeners);
        this.updateListeners = List.copyOf(builder.updateListeners);
        this.errorListeners = List.copyOf(builder.errorListeners);
        this.redactor = builder.redactor;
    }

    public static DiagnosticsHooks noop() {
        return NOOP;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String newCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public String redact(String value) {
        return redactor.redact(value);
    }

    public void onApiRequest(BotApiRequestEvent event) {
        for (BotApiRequestListener listener : requestListeners) {
            safe(() -> listener.onRequest(event));
        }
    }

    public void onApiResponse(BotApiResponseEvent event) {
        for (BotApiResponseListener listener : responseListeners) {
            safe(() -> listener.onResponse(event));
        }
    }

    public void onUpdateStarted(UpdateProcessingStartedEvent event) {
        for (UpdateProcessingListener listener : updateListeners) {
            safe(() -> listener.onUpdateStarted(event));
        }
    }

    public void onUpdateFinished(UpdateProcessingFinishedEvent event) {
        for (UpdateProcessingListener listener : updateListeners) {
            safe(() -> listener.onUpdateFinished(event));
        }
    }

    public void onError(DiagnosticErrorEvent event) {
        for (ErrorListener listener : errorListeners) {
            safe(() -> listener.onError(event));
        }
    }

    private static void safe(Runnable callback) {
        try {
            callback.run();
        } catch (RuntimeException ignored) {
            // Listener failure must not break bot runtime pipeline.
        }
    }

    public static final class Builder {

        private final List<BotApiRequestListener> requestListeners = new ArrayList<>();
        private final List<BotApiResponseListener> responseListeners = new ArrayList<>();
        private final List<UpdateProcessingListener> updateListeners = new ArrayList<>();
        private final List<ErrorListener> errorListeners = new ArrayList<>();
        private SensitiveDataRedactor redactor = DefaultSensitiveDataRedactor.INSTANCE;

        private Builder() {
        }

        public Builder addRequestListener(BotApiRequestListener listener) {
            requestListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
            return this;
        }

        public Builder addResponseListener(BotApiResponseListener listener) {
            responseListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
            return this;
        }

        public Builder addUpdateProcessingListener(UpdateProcessingListener listener) {
            updateListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
            return this;
        }

        public Builder addErrorListener(ErrorListener listener) {
            errorListeners.add(Objects.requireNonNull(listener, "listener must not be null"));
            return this;
        }

        public Builder redactor(SensitiveDataRedactor redactor) {
            this.redactor = DefaultSensitiveDataRedactor.nonNull(redactor);
            return this;
        }

        public DiagnosticsHooks build() {
            return new DiagnosticsHooks(this);
        }
    }
}
