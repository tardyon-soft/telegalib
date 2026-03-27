package ru.tardyon.botframework.telegram.diagnostics;

@FunctionalInterface
public interface ErrorListener {

    void onError(DiagnosticErrorEvent event);
}
