package ru.tardyon.botframework.telegram.api.transport;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record TelegramHttpRequest(
    String method,
    URI uri,
    Map<String, List<String>> headers,
    byte[] body
) {
    public TelegramHttpRequest {
        Objects.requireNonNull(method, "method must not be null");
        Objects.requireNonNull(uri, "uri must not be null");
        headers = headers == null ? Map.of() : Map.copyOf(headers);
        body = body == null ? new byte[0] : body.clone();
    }
}
