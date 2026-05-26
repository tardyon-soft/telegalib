package ru.tardyon.botframework.telegram.api.transport;

import java.util.List;
import java.util.Map;

public record TelegramHttpResponse(
    int statusCode,
    Map<String, List<String>> headers,
    byte[] body
) {
}
