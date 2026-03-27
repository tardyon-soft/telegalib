package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;
import java.util.Optional;

public record ScreenCallbackData(
    String namespace,
    String action,
    String payload
) {
    public static final String PREFIX = "screen:";
    public static final String NAV_NAMESPACE = "nav";
    public static final String ACTION_BACK = "back";

    public ScreenCallbackData {
        Objects.requireNonNull(namespace, "namespace must not be null");
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
    }

    public static Optional<ScreenCallbackData> parse(String data) {
        if (data == null || data.isBlank() || !data.startsWith(PREFIX)) {
            return Optional.empty();
        }
        String raw = data.substring(PREFIX.length());
        String[] parts = raw.split(":", 3);
        if (parts.length < 2) {
            return Optional.empty();
        }
        String payload = parts.length == 3 ? parts[2] : "";
        return Optional.of(new ScreenCallbackData(parts[0], parts[1], payload));
    }

    public boolean isBack() {
        return NAV_NAMESPACE.equals(namespace) && ACTION_BACK.equals(action);
    }

    public static String back() {
        return PREFIX + NAV_NAMESPACE + ":" + ACTION_BACK;
    }

    public static String of(String namespace, String action, String payload) {
        Objects.requireNonNull(namespace, "namespace must not be null");
        Objects.requireNonNull(action, "action must not be null");
        String safePayload = payload == null ? "" : payload;
        return PREFIX + namespace + ":" + action + ":" + safePayload;
    }
}
