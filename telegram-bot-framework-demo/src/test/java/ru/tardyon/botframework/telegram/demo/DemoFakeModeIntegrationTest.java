package ru.tardyon.botframework.telegram.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.testkit.assertion.RequestAssertions;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiServer;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("fake")
class DemoFakeModeIntegrationTest {

    private static final FakeBotApiServer FAKE_SERVER = new FakeBotApiServer().start();

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("telegram.bot.token", () -> "fake-test-token");
        registry.add("telegram.bot.transport.base-url", FAKE_SERVER::baseUrl);
        registry.add("telegram.bot.polling.enabled", () -> "false");
    }

    @AfterAll
    static void stopFakeServer() {
        FAKE_SERVER.stop();
    }

    @Autowired
    private TelegramApiClient telegramApiClient;

    @Test
    void runsDemoAgainstFakeBotApiServer() {
        FAKE_SERVER.enqueueResponse(
            "sendMessage",
            FakeBotApiResponse.okJson(
                "{\"message_id\":101,\"date\":1710000000,\"chat\":{\"id\":1,\"type\":\"private\"},\"text\":\"demo-ok\"}"
            )
        );

        Message message = telegramApiClient.sendMessage(SendMessageRequest.of(1L, "hello-fake"));

        assertNotNull(message);
        assertEquals("demo-ok", message.text());
        RequestAssertions.assertMethodCalled(FAKE_SERVER, "sendMessage");
        RequestAssertions.assertRequestBodyContains(FAKE_SERVER, "sendMessage", "\"text\":\"hello-fake\"");
    }
}
