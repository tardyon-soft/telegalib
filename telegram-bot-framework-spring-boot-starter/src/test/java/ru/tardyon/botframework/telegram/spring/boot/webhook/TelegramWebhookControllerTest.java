package ru.tardyon.botframework.telegram.spring.boot.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import ru.tardyon.botframework.telegram.webhook.WebhookSecurityException;
import ru.tardyon.botframework.telegram.webhook.WebhookUpdateProcessor;

class TelegramWebhookControllerTest {

    @Test
    void returnsOkWhenProcessorSucceeds() {
        AtomicInteger calls = new AtomicInteger();
        WebhookUpdateProcessor processor = (raw, metadata) -> calls.incrementAndGet();
        TelegramWebhookController controller = new TelegramWebhookController(processor);

        ResponseEntity<Void> response = controller.handleUpdate(
            "{\"update_id\":1}",
            new LinkedMultiValueMap<>()
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, calls.get());
    }

    @Test
    void returnsForbiddenOnSecretTokenFailure() {
        WebhookUpdateProcessor processor = (raw, metadata) -> {
            throw new WebhookSecurityException("invalid");
        };
        TelegramWebhookController controller = new TelegramWebhookController(processor);

        ResponseEntity<Void> response = controller.handleUpdate(
            "{\"update_id\":1}",
            new LinkedMultiValueMap<>()
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void returnsBadRequestOnInvalidPayload() {
        WebhookUpdateProcessor processor = (raw, metadata) -> {
            throw new IllegalArgumentException("bad payload");
        };
        TelegramWebhookController controller = new TelegramWebhookController(processor);

        ResponseEntity<Void> response = controller.handleUpdate(
            "{",
            new LinkedMultiValueMap<>()
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void passesHeadersToProcessorMetadata() {
        AtomicInteger validations = new AtomicInteger();
        WebhookUpdateProcessor processor = (raw, metadata) -> {
            String token = metadata.firstHeader("X-Telegram-Bot-Api-Secret-Token");
            if ("secret-123".equals(token)) {
                validations.incrementAndGet();
            } else {
                throw new WebhookSecurityException("invalid");
            }
        };
        TelegramWebhookController controller = new TelegramWebhookController(processor);
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Telegram-Bot-Api-Secret-Token", "secret-123");

        ResponseEntity<Void> response = controller.handleUpdate("{\"update_id\":2}", headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, validations.get());
    }
}
