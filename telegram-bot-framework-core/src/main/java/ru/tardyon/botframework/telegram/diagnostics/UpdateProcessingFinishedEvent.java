package ru.tardyon.botframework.telegram.diagnostics;

import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

/**
 * Update processing completion event.
 */
public record UpdateProcessingFinishedEvent(
    String correlationId,
    Long updateId,
    UpdateContext.UpdateType updateType,
    String source,
    long durationMillis,
    boolean success
) {
}
