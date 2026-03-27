package ru.tardyon.botframework.telegram.diagnostics;

@FunctionalInterface
public interface BotApiResponseListener {

    void onResponse(BotApiResponseEvent event);
}
