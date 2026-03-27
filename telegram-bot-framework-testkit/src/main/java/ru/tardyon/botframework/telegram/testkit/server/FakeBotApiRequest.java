package ru.tardyon.botframework.telegram.testkit.server;

import java.util.List;
import java.util.Map;

/**
 * Recorded incoming HTTP call for fake Bot API assertions.
 */
public record FakeBotApiRequest(
    long sequence,
    String httpMethod,
    String path,
    String telegramMethod,
    String body,
    Map<String, List<String>> headers
) {
}
