package ru.tardyon.botframework.telegram.testkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.DefaultTelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.testkit.assertion.RequestAssertions;
import ru.tardyon.botframework.telegram.testkit.fixture.TelegramJsonFixtures;
import ru.tardyon.botframework.telegram.testkit.fixture.UpdateFixtures;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiServer;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiResponse;
import ru.tardyon.botframework.telegram.testkit.update.PollingUpdateSimulator;
import ru.tardyon.botframework.telegram.testkit.update.WebhookDeliveryResult;
import ru.tardyon.botframework.telegram.testkit.update.WebhookSimulator;
import ru.tardyon.botframework.telegram.webhook.WebhookHeaders;

class FakeBotApiServerIntegrationTest {

    @Test
    void assertSendMessageRequestWithRecordedCall() {
        try (FakeBotApiServer server = new FakeBotApiServer().start()) {
            server.enqueueResponse(
                "sendMessage",
                FakeBotApiResponse.okJson("{\"message_id\":10,\"date\":1710000000,\"chat\":{\"id\":123,\"type\":\"private\"},\"text\":\"ok\"}")
            );

            DefaultTelegramApiClient client = new DefaultTelegramApiClient("TOKEN", server.baseUrl(), java.net.http.HttpClient.newHttpClient(), new ObjectMapper());
            Message response = client.sendMessage(SendMessageRequest.of(123L, "hello"));

            assertNotNull(response);
            assertEquals("ok", response.text());
            RequestAssertions.assertMethodCalled(server, "sendMessage");
            RequestAssertions.assertRequestBodyContains(server, "sendMessage", "\"text\":\"hello\"");
        }
    }

    @Test
    void simulatePollingCallbackUpdate() {
        PollingUpdateSimulator polling = new PollingUpdateSimulator()
            .enqueue(TelegramJsonFixtures.update("fixtures/update/callback_update.json"));

        try (FakeBotApiServer server = new FakeBotApiServer().attachPollingSimulator(polling).start()) {
            DefaultTelegramApiClient client = new DefaultTelegramApiClient("TOKEN", server.baseUrl(), java.net.http.HttpClient.newHttpClient(), new ObjectMapper());

            List<Update> updates = client.getUpdates(new GetUpdatesRequest(null, 10, 0, null));

            assertEquals(1, updates.size());
            assertNotNull(updates.getFirst().callbackQuery());
            assertEquals("menu:root", updates.getFirst().callbackQuery().data());
            RequestAssertions.assertMethodCalled(server, "getUpdates");
        }
    }

    @Test
    void simulateWebhookDeliveryWithSecretHeader() throws Exception {
        AtomicReference<String> headerValue = new AtomicReference<>();
        AtomicReference<String> requestBody = new AtomicReference<>();

        com.sun.net.httpserver.HttpServer webhookReceiver = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(0), 0);
        webhookReceiver.createContext("/webhook", exchange -> {
            headerValue.set(exchange.getRequestHeaders().getFirst(WebhookHeaders.TELEGRAM_SECRET_TOKEN_HEADER));
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
            byte[] response = "OK".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        webhookReceiver.start();

        try {
            String webhookUrl = "http://127.0.0.1:" + webhookReceiver.getAddress().getPort() + "/webhook";
            WebhookSimulator simulator = new WebhookSimulator();
            WebhookDeliveryResult result = simulator.sendUpdate(
                webhookUrl,
                UpdateFixtures.callbackUpdate(9001L, "cbq-9001", 222L, 333L, "menu:test"),
                "super-secret"
            );

            assertEquals(200, result.statusCode());
            assertEquals("super-secret", headerValue.get());
            assertNotNull(requestBody.get());
            assertNotNull(TelegramJsonFixtures.readValue("fixtures/update/callback_update.json", Update.class));
        } finally {
            webhookReceiver.stop(0);
        }
    }
}
