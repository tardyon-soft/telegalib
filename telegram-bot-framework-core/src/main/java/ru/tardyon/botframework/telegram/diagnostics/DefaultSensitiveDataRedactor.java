package ru.tardyon.botframework.telegram.diagnostics;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Minimal string-based redactor for known sensitive Bot API fields.
 */
public final class DefaultSensitiveDataRedactor implements SensitiveDataRedactor {

    private static final Pattern PROVIDER_TOKEN = Pattern.compile("(\\\"provider_token\\\"\\s*:\\s*\\\")([^\\\"]*)(\\\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEBHOOK_SECRET_TOKEN = Pattern.compile("(\\\"secret_token\\\"\\s*:\\s*\\\")([^\\\"]*)(\\\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern PROVIDER_DATA = Pattern.compile("(\\\"provider_data\\\"\\s*:\\s*)(\\\"[^\\\"]*\\\"|\\{[^{}]*}|\\[[^\\[\\]]*])", Pattern.CASE_INSENSITIVE);
    private static final Pattern BOT_TOKEN_IN_PATH = Pattern.compile("/bot[0-9]+:[A-Za-z0-9_\\-]+", Pattern.CASE_INSENSITIVE);

    public static final DefaultSensitiveDataRedactor INSTANCE = new DefaultSensitiveDataRedactor();

    private DefaultSensitiveDataRedactor() {
    }

    @Override
    public String redact(String raw) {
        if (raw == null || raw.isEmpty()) {
            return raw;
        }
        String redacted = raw;
        redacted = PROVIDER_TOKEN.matcher(redacted).replaceAll("$1<redacted>$3");
        redacted = WEBHOOK_SECRET_TOKEN.matcher(redacted).replaceAll("$1<redacted>$3");
        redacted = PROVIDER_DATA.matcher(redacted).replaceAll("$1\"<redacted>\"");
        redacted = BOT_TOKEN_IN_PATH.matcher(redacted).replaceAll("/bot<redacted>");
        return redacted;
    }

    public static String redactNullable(String value) {
        return INSTANCE.redact(value);
    }

    public static SensitiveDataRedactor nonNull(SensitiveDataRedactor redactor) {
        return Objects.requireNonNull(redactor, "redactor must not be null");
    }
}
