package ru.tardyon.botframework.telegram.webhook;

public class WebhookSecurityException extends RuntimeException {

    public WebhookSecurityException(String message) {
        super(message);
    }
}
