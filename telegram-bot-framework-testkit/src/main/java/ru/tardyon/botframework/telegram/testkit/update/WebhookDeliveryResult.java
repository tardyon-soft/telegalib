package ru.tardyon.botframework.telegram.testkit.update;

import java.util.List;
import java.util.Map;

/**
 * Result of test webhook delivery.
 */
public record WebhookDeliveryResult(
    int statusCode,
    String responseBody,
    Map<String, List<String>> responseHeaders
) {
}
