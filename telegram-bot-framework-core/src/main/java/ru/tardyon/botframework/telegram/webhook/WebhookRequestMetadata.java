package ru.tardyon.botframework.telegram.webhook;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public record WebhookRequestMetadata(
    Map<String, List<String>> headers
) {
    public static WebhookRequestMetadata empty() {
        return new WebhookRequestMetadata(Map.of());
    }

    public String firstHeader(String headerName) {
        if (headers == null || headerName == null) {
            return null;
        }
        String expected = headerName.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().toLowerCase(Locale.ROOT).equals(expected)) {
                List<String> values = entry.getValue();
                if (values == null || values.isEmpty()) {
                    return null;
                }
                return values.get(0);
            }
        }
        return null;
    }
}
