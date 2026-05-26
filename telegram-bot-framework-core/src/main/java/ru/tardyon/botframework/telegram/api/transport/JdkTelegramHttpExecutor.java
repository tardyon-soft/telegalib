package ru.tardyon.botframework.telegram.api.transport;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public final class JdkTelegramHttpExecutor implements TelegramHttpExecutor {

    private final HttpClient httpClient;

    public JdkTelegramHttpExecutor(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    @Override
    public TelegramHttpResponse execute(TelegramHttpRequest request) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(request.uri())
            .method(request.method(), HttpRequest.BodyPublishers.ofByteArray(request.body()));
        request.headers().forEach((name, values) -> values.forEach(value -> builder.header(name, value)));

        HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        return new TelegramHttpResponse(response.statusCode(), response.headers().map(), response.body());
    }
}
