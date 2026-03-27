package ru.tardyon.botframework.telegram.spring.boot.webhook;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.tardyon.botframework.telegram.webhook.WebhookRequestMetadata;
import ru.tardyon.botframework.telegram.webhook.WebhookSecurityException;
import ru.tardyon.botframework.telegram.webhook.WebhookUpdateProcessor;

@RestController
public class TelegramWebhookController {

    private final WebhookUpdateProcessor webhookUpdateProcessor;

    public TelegramWebhookController(WebhookUpdateProcessor webhookUpdateProcessor) {
        this.webhookUpdateProcessor = webhookUpdateProcessor;
    }

    @PostMapping("${telegram.bot.webhook.path:/telegram/webhook}")
    public ResponseEntity<Void> handleUpdate(
        @RequestBody String rawBody,
        @RequestHeader MultiValueMap<String, String> headers
    ) {
        try {
            webhookUpdateProcessor.process(rawBody, new WebhookRequestMetadata(copyHeaders(headers)));
            return ResponseEntity.ok().build();
        } catch (WebhookSecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private static Map<String, List<String>> copyHeaders(MultiValueMap<String, String> headers) {
        Map<String, List<String>> copied = new LinkedHashMap<>();
        headers.forEach((key, value) -> copied.put(key, List.copyOf(value)));
        return copied;
    }
}
