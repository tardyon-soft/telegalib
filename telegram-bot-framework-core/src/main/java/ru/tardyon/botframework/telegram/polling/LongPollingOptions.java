package ru.tardyon.botframework.telegram.polling;

import java.util.List;

public record LongPollingOptions(
    int timeoutSeconds,
    int limit,
    List<String> allowedUpdates,
    long errorBackoffMillis
) {

    public static LongPollingOptions defaults() {
        return new LongPollingOptions(30, 100, null, 1000L);
    }

    public LongPollingOptions {
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("timeoutSeconds must be > 0 for long polling");
        }
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit must be in range [1, 100]");
        }
        if (errorBackoffMillis < 0) {
            throw new IllegalArgumentException("errorBackoffMillis must be >= 0");
        }
        allowedUpdates = allowedUpdates == null ? null : List.copyOf(allowedUpdates);
    }
}
