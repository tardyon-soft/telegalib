package ru.tardyon.botframework.telegram.testkit.update;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.webhook.WebhookHeaders;

/**
 * Lightweight webhook sender for integration tests.
 */
public final class WebhookSimulator {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WebhookSimulator() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    public WebhookSimulator(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    public WebhookDeliveryResult sendUpdate(String webhookUrl, Update update) {
        return sendUpdate(webhookUrl, update, null);
    }

    public WebhookDeliveryResult sendUpdate(String webhookUrl, Update update, String secretToken) {
        Objects.requireNonNull(update, "update must not be null");
        try {
            String payload = objectMapper.writeValueAsString(update);
            return sendJson(webhookUrl, payload, secretToken);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize webhook Update payload", e);
        }
    }

    public WebhookDeliveryResult sendJson(String webhookUrl, String jsonPayload) {
        return sendJson(webhookUrl, jsonPayload, null);
    }

    public WebhookDeliveryResult sendJson(String webhookUrl, String jsonPayload, String secretToken) {
        Objects.requireNonNull(webhookUrl, "webhookUrl must not be null");
        Objects.requireNonNull(jsonPayload, "jsonPayload must not be null");

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(webhookUrl))
            .header("Content-Type", "application/json; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8));

        if (secretToken != null && !secretToken.isBlank()) {
            builder.header(WebhookHeaders.TELEGRAM_SECRET_TOKEN_HEADER, secretToken);
        }

        try {
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new WebhookDeliveryResult(response.statusCode(), response.body(), response.headers().map());
        } catch (IOException e) {
            throw new IllegalStateException("I/O error while simulating webhook delivery", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while simulating webhook delivery", e);
        }
    }
}
