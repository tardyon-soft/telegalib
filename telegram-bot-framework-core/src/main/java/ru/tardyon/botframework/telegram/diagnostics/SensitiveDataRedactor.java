package ru.tardyon.botframework.telegram.diagnostics;

@FunctionalInterface
public interface SensitiveDataRedactor {

    String redact(String raw);
}
