package ru.tardyon.botframework.telegram.diagnostics;

import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

/**
 * Update processing start event.
 */
public record UpdateProcessingStartedEvent(
    String correlationId,
    Long updateId,
    UpdateContext.UpdateType updateType,
    String source,
    long timestampEpochMillis
) {
}
