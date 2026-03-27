package ru.tardyon.botframework.telegram.diagnostics;

/**
 * Outgoing Bot API request event.
 */
public record BotApiRequestEvent(
    String correlationId,
    String methodName,
    long timestampEpochMillis,
    String redactedRequestBody
) {
}
