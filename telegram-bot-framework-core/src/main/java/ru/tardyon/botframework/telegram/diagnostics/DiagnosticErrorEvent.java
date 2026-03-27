package ru.tardyon.botframework.telegram.diagnostics;

/**
 * Generic diagnostics error event from API/polling/webhook/dispatch pipeline.
 */
public record DiagnosticErrorEvent(
    String correlationId,
    String component,
    String operation,
    Long updateId,
    String methodName,
    Throwable error
) {
}
