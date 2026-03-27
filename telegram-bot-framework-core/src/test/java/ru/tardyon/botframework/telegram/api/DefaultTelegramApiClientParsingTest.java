package ru.tardyon.botframework.telegram.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.WebhookInfo;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.transport.TelegramApiResponse;
import ru.tardyon.botframework.telegram.exception.TelegramApiException;

class DefaultTelegramApiClientParsingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parseSuccessResponse() {
        String raw = """
            {
              "ok": true,
              "result": {
                "id": 123456789,
                "is_bot": true,
                "first_name": "TestBot",
                "username": "test_bot"
              }
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructType(User.class);
        TelegramApiResponse<User> response = DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertNotNull(response.result());
        assertEquals(123456789L, response.result().id());
        assertEquals("TestBot", response.result().firstName());
    }

    @Test
    void parseErrorResponse() {
        String raw = """
            {
              "ok": false,
              "error_code": 400,
              "description": "Bad Request: chat not found"
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructType(Boolean.class);
        TelegramApiResponse<Boolean> response = DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertFalse(response.ok());
        assertEquals(400, response.errorCode());
        assertEquals("Bad Request: chat not found", response.description());
    }

    @Test
    void parseGetUpdatesResponseWithMessage() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1001,
                  "message": {
                    "message_id": 42,
                    "date": 1710000000,
                    "text": "ping",
                    "chat": {
                      "id": 123456789,
                      "type": "private"
                    }
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertEquals(1, response.result().size());
        Update update = response.result().get(0);
        assertEquals(1001L, update.updateId());
        assertNotNull(update.message());
        assertEquals("ping", update.message().text());
    }

    @Test
    void parseGetUpdatesResponseWithCallbackQuery() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1002,
                  "callback_query": {
                    "id": "cbq-1",
                    "chat_instance": "ci-1",
                    "data": "menu:settings",
                    "from": {
                      "id": 777,
                      "is_bot": false,
                      "first_name": "Alex"
                    }
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertEquals(1, response.result().size());
        Update update = response.result().get(0);
        assertNotNull(update.callbackQuery());
        assertEquals("cbq-1", update.callbackQuery().id());
        assertEquals("menu:settings", update.callbackQuery().data());
    }

    @Test
    void parseGetUpdatesResponseWithInlineQuery() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1003,
                  "inline_query": {
                    "id": "iq-1",
                    "from": {
                      "id": 777,
                      "is_bot": false,
                      "first_name": "Alex"
                    },
                    "query": "java",
                    "offset": "10",
                    "chat_type": "sender"
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertEquals(1, response.result().size());
        Update update = response.result().get(0);
        assertNotNull(update.inlineQuery());
        assertEquals("iq-1", update.inlineQuery().id());
        assertEquals("java", update.inlineQuery().query());
    }

    @Test
    void parseGetUpdatesResponseWithChosenInlineResult() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1004,
                  "chosen_inline_result": {
                    "result_id": "res-1",
                    "from": {
                      "id": 777,
                      "is_bot": false,
                      "first_name": "Alex"
                    },
                    "query": "java"
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertEquals(1, response.result().size());
        Update update = response.result().get(0);
        assertNotNull(update.chosenInlineResult());
        assertEquals("res-1", update.chosenInlineResult().resultId());
        assertEquals("java", update.chosenInlineResult().query());
    }

    @Test
    void parseGetUpdatesResponseWithShippingQuery() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1005,
                  "shipping_query": {
                    "id": "ship-q-1",
                    "invoice_payload": "invoice:basic",
                    "from": {
                      "id": 777,
                      "is_bot": false,
                      "first_name": "Alex"
                    },
                    "shipping_address": {
                      "country_code": "US",
                      "state": "CA",
                      "city": "San Francisco",
                      "street_line1": "Market st",
                      "street_line2": "Suite 100",
                      "post_code": "94103"
                    }
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        Update update = response.result().getFirst();
        assertNotNull(update.shippingQuery());
        assertEquals("ship-q-1", update.shippingQuery().id());
        assertEquals("invoice:basic", update.shippingQuery().invoicePayload());
        assertEquals("US", update.shippingQuery().shippingAddress().countryCode());
    }

    @Test
    void parseGetUpdatesResponseWithPreCheckoutQuery() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1006,
                  "pre_checkout_query": {
                    "id": "pcq-1",
                    "currency": "XTR",
                    "total_amount": 500,
                    "invoice_payload": "stars:pro",
                    "from": {
                      "id": 777,
                      "is_bot": false,
                      "first_name": "Alex"
                    }
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        Update update = response.result().getFirst();
        assertNotNull(update.preCheckoutQuery());
        assertEquals("pcq-1", update.preCheckoutQuery().id());
        assertEquals("XTR", update.preCheckoutQuery().currency());
        assertEquals("stars:pro", update.preCheckoutQuery().invoicePayload());
    }

    @Test
    void parseMessageWithSuccessfulPayment() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1007,
                  "message": {
                    "message_id": 42,
                    "date": 1710000000,
                    "chat": { "id": 123456789, "type": "private" },
                    "successful_payment": {
                      "currency": "XTR",
                      "total_amount": 500,
                      "invoice_payload": "stars:pro",
                      "telegram_payment_charge_id": "tg-charge-1",
                      "provider_payment_charge_id": "provider-charge-1"
                    }
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        Update update = response.result().getFirst();
        assertNotNull(update.message());
        assertNotNull(update.message().successfulPayment());
        assertEquals("XTR", update.message().successfulPayment().currency());
        assertEquals("stars:pro", update.message().successfulPayment().invoicePayload());
    }

    @Test
    void parseMessageWithWebAppData() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 1008,
                  "message": {
                    "message_id": 43,
                    "date": 1710000001,
                    "chat": { "id": 123456789, "type": "private" },
                    "web_app_data": {
                      "data": "{\\\"k\\\":\\\"v\\\"}",
                      "button_text": "Open app"
                    }
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        Update update = response.result().getFirst();
        assertNotNull(update.message());
        assertNotNull(update.message().webAppData());
        assertEquals("{\"k\":\"v\"}", update.message().webAppData().data());
        assertEquals("Open app", update.message().webAppData().buttonText());
    }

    @Test
    void parseGetUpdatesResponseWithBusinessConnectionAndMessages() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "update_id": 2001,
                  "business_connection": {
                    "id": "bc-1",
                    "user": { "id": 777, "is_bot": false, "first_name": "Alex" },
                    "user_chat_id": 9001,
                    "date": 1710000000,
                    "is_enabled": true
                  }
                },
                {
                  "update_id": 2002,
                  "business_message": {
                    "business_connection_id": "bc-1",
                    "message_id": 5,
                    "date": 1710000001,
                    "chat": { "id": 123, "type": "private" },
                    "text": "hello"
                  }
                },
                {
                  "update_id": 2003,
                  "edited_business_message": {
                    "business_connection_id": "bc-1",
                    "message_id": 6,
                    "date": 1710000002,
                    "chat": { "id": 123, "type": "private" },
                    "text": "edited"
                  }
                },
                {
                  "update_id": 2004,
                  "deleted_business_messages": {
                    "business_connection_id": "bc-1",
                    "chat": { "id": 123, "type": "private" },
                    "message_ids": [5, 6]
                  }
                }
              ]
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        TelegramApiResponse<List<Update>> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertEquals("bc-1", response.result().get(0).businessConnection().id());
        assertEquals("bc-1", response.result().get(1).businessMessage().businessConnectionId());
        assertEquals("edited", response.result().get(2).editedBusinessMessage().text());
        assertEquals(List.of(5, 6), response.result().get(3).deletedBusinessMessages().messageIds());
    }

    @Test
    void parseEditMessageTextBooleanResult() {
        String raw = """
            {
              "ok": true,
              "result": true
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructType(EditMessageTextResult.class);
        TelegramApiResponse<EditMessageTextResult> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertNotNull(response.result());
        assertTrue(response.result().isSuccessful());
        assertFalse(response.result().hasMessage());
    }

    @Test
    void parseEditMessageReplyMarkupBooleanResult() {
        String raw = """
            {
              "ok": true,
              "result": true
            }
            """;

        JavaType resultType = objectMapper.getTypeFactory().constructType(EditMessageReplyMarkupResult.class);
        TelegramApiResponse<EditMessageReplyMarkupResult> response =
            DefaultTelegramApiClient.parseApiResponse(raw, resultType, objectMapper);

        assertTrue(response.ok());
        assertNotNull(response.result());
        assertTrue(response.result().isSuccessful());
        assertFalse(response.result().hasMessage());
    }

    @Test
    void throwsTelegramApiExceptionWhenApiResponseIsError() {
        String raw = """
            {
              "ok": false,
              "error_code": 401,
              "description": "Unauthorized"
            }
            """;

        DefaultTelegramApiClient apiClient = new DefaultTelegramApiClient(
            "test-token",
            "https://example.test",
            new StubHttpClient(raw),
            objectMapper
        );

        TelegramApiException ex = assertThrows(TelegramApiException.class, apiClient::getMe);
        assertEquals(401, ex.getErrorCode());
        assertEquals("Unauthorized", ex.getDescription());
        assertEquals(raw.strip(), ex.getRawBody().strip());
    }

    @Test
    void parseGetWebhookInfoResponse() {
        String raw = """
            {
              "ok": true,
              "result": {
                "url": "https://example.com/hook",
                "has_custom_certificate": false,
                "pending_update_count": 2,
                "max_connections": 40
              }
            }
            """;

        DefaultTelegramApiClient apiClient = new DefaultTelegramApiClient(
            "test-token",
            "https://example.test",
            new StubHttpClient(raw),
            objectMapper
        );

        WebhookInfo info = apiClient.getWebhookInfo();
        assertEquals("https://example.com/hook", info.url());
        assertEquals(false, info.hasCustomCertificate());
        assertEquals(2, info.pendingUpdateCount());
        assertEquals(40, info.maxConnections());
    }

    @Test
    void parseGetMyCommandsResponse() {
        String raw = """
            {
              "ok": true,
              "result": [
                {
                  "command": "start",
                  "description": "Start bot"
                },
                {
                  "command": "help",
                  "description": "Show help"
                }
              ]
            }
            """;

        DefaultTelegramApiClient apiClient = new DefaultTelegramApiClient(
            "test-token",
            "https://example.test",
            new StubHttpClient(raw),
            objectMapper
        );

        List<BotCommand> commands = apiClient.getMyCommands(null);
        assertEquals(2, commands.size());
        assertEquals("start", commands.get(0).command());
        assertEquals("Show help", commands.get(1).description());
    }

    private static final class StubHttpClient extends HttpClient {

        private final String responseBody;

        private StubHttpClient(String responseBody) {
            this.responseBody = responseBody;
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException {
            HttpResponse<String> response = new StubHttpResponse(request, responseBody);
            @SuppressWarnings("unchecked")
            HttpResponse<T> casted = (HttpResponse<T>) response;
            return casted;
        }

        @Override
        public <T> java.util.concurrent.CompletableFuture<HttpResponse<T>> sendAsync(
            HttpRequest request,
            HttpResponse.BodyHandler<T> responseBodyHandler
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> java.util.concurrent.CompletableFuture<HttpResponse<T>> sendAsync(
            HttpRequest request,
            HttpResponse.BodyHandler<T> responseBodyHandler,
            HttpResponse.PushPromiseHandler<T> pushPromiseHandler
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<java.net.CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<java.time.Duration> connectTimeout() {
            return Optional.empty();
        }

        @Override
        public Redirect followRedirects() {
            return Redirect.NEVER;
        }

        @Override
        public Optional<java.net.ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public javax.net.ssl.SSLContext sslContext() {
            return null;
        }

        @Override
        public javax.net.ssl.SSLParameters sslParameters() {
            return null;
        }

        @Override
        public Optional<java.net.Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public Version version() {
            return Version.HTTP_1_1;
        }

        @Override
        public Optional<java.util.concurrent.Executor> executor() {
            return Optional.empty();
        }
    }

    private record StubHttpResponse(HttpRequest request, String body) implements HttpResponse<String> {
        @Override
        public int statusCode() {
            return 200;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public Optional<HttpResponse<String>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(java.util.Map.of(), (name, value) -> true);
        }

        @Override
        public String body() {
            return body;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return request.uri();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
