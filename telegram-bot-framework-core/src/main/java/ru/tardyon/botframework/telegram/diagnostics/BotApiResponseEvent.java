package ru.tardyon.botframework.telegram.diagnostics;

/**
 * Outgoing Bot API response event.
 */
public record BotApiResponseEvent(
    String correlationId,
    String methodName,
    long durationMillis,
    boolean success,
    Integer httpStatus,
    Integer telegramErrorCode,
    String telegramDescription,
    String redactedResponseBody
) {
}
