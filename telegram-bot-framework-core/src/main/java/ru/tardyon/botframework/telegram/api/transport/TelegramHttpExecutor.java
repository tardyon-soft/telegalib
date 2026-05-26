package ru.tardyon.botframework.telegram.api.transport;

import java.io.IOException;

public interface TelegramHttpExecutor {

    TelegramHttpResponse execute(TelegramHttpRequest request) throws IOException, InterruptedException;
}
