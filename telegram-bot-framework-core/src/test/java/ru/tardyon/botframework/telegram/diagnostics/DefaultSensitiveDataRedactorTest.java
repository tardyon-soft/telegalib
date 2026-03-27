package ru.tardyon.botframework.telegram.diagnostics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DefaultSensitiveDataRedactorTest {

    @Test
    void redactsSensitiveBotApiFields() {
        String raw = """
            {
              "provider_token":"prov-token-1",
              "provider_data":"sensitive-payment-payload",
              "secret_token":"wh-secret",
              "url":"https://api.telegram.org/bot123456:ABCDEF/sendMessage"
            }
            """;

        String redacted = DefaultSensitiveDataRedactor.INSTANCE.redact(raw);

        assertTrue(redacted.contains("<redacted>"));
        assertFalse(redacted.contains("prov-token-1"));
        assertFalse(redacted.contains("sensitive-payment-payload"));
        assertFalse(redacted.contains("wh-secret"));
        assertFalse(redacted.contains("bot123456:ABCDEF"));
    }
}
