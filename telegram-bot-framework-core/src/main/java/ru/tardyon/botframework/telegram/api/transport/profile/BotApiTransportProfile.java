package ru.tardyon.botframework.telegram.api.transport.profile;

import java.util.Objects;

/**
 * Transport profile configuration for Telegram Bot API client.
 */
public record BotApiTransportProfile(
    BotApiTransportMode mode,
    String baseUrl,
    boolean localFileUriUploadEnabled
) {

    public static final String DEFAULT_CLOUD_BASE_URL = "https://api.telegram.org";

    public BotApiTransportProfile {
        mode = Objects.requireNonNull(mode, "mode must not be null");
        baseUrl = requireText(baseUrl, "baseUrl");
    }

    public static BotApiTransportProfile cloudDefault() {
        return cloud(DEFAULT_CLOUD_BASE_URL);
    }

    public static BotApiTransportProfile cloud(String baseUrl) {
        return new BotApiTransportProfile(BotApiTransportMode.CLOUD, baseUrl, false);
    }

    public static BotApiTransportProfile local(String baseUrl) {
        return new BotApiTransportProfile(BotApiTransportMode.LOCAL, baseUrl, true);
    }

    public static BotApiTransportProfile local(String baseUrl, boolean localFileUriUploadEnabled) {
        return new BotApiTransportProfile(BotApiTransportMode.LOCAL, baseUrl, localFileUriUploadEnabled);
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
