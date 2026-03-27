package ru.tardyon.botframework.telegram.webapp;

import java.time.Instant;
import java.util.Map;

public record WebAppInitDataValidationResult(
    boolean valid,
    Map<String, String> fields,
    Long authDate,
    Instant authDateInstant,
    String error
) {
}
