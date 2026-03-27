package ru.tardyon.botframework.telegram.diagnostics;

@FunctionalInterface
public interface BotApiRequestListener {

    void onRequest(BotApiRequestEvent event);
}
