package ru.tardyon.botframework.telegram.webhook;

public interface WebhookUpdateProcessor {

    void process(String rawJsonBody, WebhookRequestMetadata metadata);
}
