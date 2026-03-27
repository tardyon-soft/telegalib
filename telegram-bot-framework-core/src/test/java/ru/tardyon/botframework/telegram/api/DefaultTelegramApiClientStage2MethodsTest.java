package ru.tardyon.botframework.telegram.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.method.AnswerInlineQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerPreCheckoutQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerShippingQueryRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SendInvoiceRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.command.BotCommandScopeDefault;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultArticle;
import ru.tardyon.botframework.telegram.api.model.inline.InputTextMessageContent;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButtons;
import ru.tardyon.botframework.telegram.api.model.payment.LabeledPrice;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingOption;

class DefaultTelegramApiClientStage2MethodsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void setWebhookUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.setWebhook(
            new SetWebhookRequest(
                "https://example.com/telegram/webhook",
                null,
                null,
                List.of("message", "callback_query"),
                true,
                "secret-token"
            )
        );

        assertTrue(result);
        assertEquals("/bottoken/setWebhook", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"url\":\"https://example.com/telegram/webhook\""));
        assertTrue(body.contains("\"allowed_updates\":[\"message\",\"callback_query\"]"));
        assertTrue(body.contains("\"drop_pending_updates\":true"));
        assertTrue(body.contains("\"secret_token\":\"secret-token\""));
    }

    @Test
    void deleteWebhookUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.deleteWebhook(new DeleteWebhookRequest(true));

        assertTrue(result);
        assertEquals("/bottoken/deleteWebhook", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"drop_pending_updates\":true"));
    }

    @Test
    void setMyCommandsSerializesCommandsAndScope() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.setMyCommands(
            new SetMyCommandsRequest(
                List.of(new BotCommand("start", "Start"), new BotCommand("help", "Help")),
                new BotCommandScopeDefault(),
                null
            )
        );

        assertTrue(result);
        assertEquals("/bottoken/setMyCommands", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"commands\""));
        assertTrue(body.contains("\"command\":\"start\""));
        assertTrue(body.contains("\"scope\":{\"type\":\"default\"}"));
    }

    @Test
    void editMessageReplyMarkupUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":true}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.editMessageReplyMarkup(
            EditMessageReplyMarkupRequest.forChatMessage(
                123L,
                10,
                Keyboards.inlineKeyboard().row(Keyboards.callbackButton("Menu", "menu:main")).build()
            )
        );

        assertEquals("/bottoken/editMessageReplyMarkup", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":123"));
        assertTrue(body.contains("\"message_id\":10"));
        assertTrue(body.contains("\"reply_markup\""));
        assertTrue(body.contains("\"callback_data\":\"menu:main\""));
    }

    @Test
    void sendDocumentWithFileIdUsesSendDocumentMethod() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":1,"chat":{"id":123,"type":"private"},"date":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendDocument(SendDocumentRequest.of(123L, InputFile.fileId("file-id-1")));

        assertEquals("/bottoken/sendDocument", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"document\":\"file-id-1\""));
    }

    @Test
    void answerInlineQueryUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        List<InlineQueryResult> results = List.of(
            new InlineQueryResultArticle("a1", "Title", InputTextMessageContent.of("Hello inline"))
        );
        boolean result = client.answerInlineQuery(new AnswerInlineQueryRequest("iq-1", results, 5, true, "n1", null));

        assertTrue(result);
        assertEquals("/bottoken/answerInlineQuery", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"inline_query_id\":\"iq-1\""));
        assertTrue(body.contains("\"results\""));
        assertTrue(body.contains("\"type\":\"article\""));
    }

    @Test
    void sendInvoiceUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":101,"chat":{"id":123,"type":"private"},"date":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendInvoice(
            new SendInvoiceRequest(
                123L,
                "Pro plan",
                "Monthly subscription",
                "invoice:pro:monthly",
                "provider-token",
                "USD",
                List.of(new LabeledPrice("Pro", 499)),
                null,
                null,
                "start-pro",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        );

        assertEquals("/bottoken/sendInvoice", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":123"));
        assertTrue(body.contains("\"title\":\"Pro plan\""));
        assertTrue(body.contains("\"currency\":\"USD\""));
        assertTrue(body.contains("\"prices\":["));
    }

    @Test
    void answerShippingQueryUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.answerShippingQuery(
            new AnswerShippingQueryRequest(
                "ship-q-1",
                true,
                List.of(new ShippingOption("pickup", "Pickup", List.of(new LabeledPrice("Pickup", 0)))),
                null
            )
        );

        assertTrue(result);
        assertEquals("/bottoken/answerShippingQuery", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"shipping_query_id\":\"ship-q-1\""));
        assertTrue(body.contains("\"ok\":true"));
        assertTrue(body.contains("\"shipping_options\""));
    }

    @Test
    void answerPreCheckoutQueryUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.answerPreCheckoutQuery(new AnswerPreCheckoutQueryRequest("pcq-1", true, null));

        assertTrue(result);
        assertEquals("/bottoken/answerPreCheckoutQuery", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"pre_checkout_query_id\":\"pcq-1\""));
        assertTrue(body.contains("\"ok\":true"));
    }

    @Test
    void setChatMenuButtonUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.setChatMenuButton(new SetChatMenuButtonRequest(321L, MenuButtons.commandsButton()));

        assertTrue(result);
        assertEquals("/bottoken/setChatMenuButton", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":321"));
        assertTrue(body.contains("\"menu_button\":{\"type\":\"commands\"}"));
    }

    @Test
    void getChatMenuButtonParsesMenuButtonResult() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"type":"web_app","text":"Open","web_app":{"url":"https://example.com/app"}}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        MenuButton result = client.getChatMenuButton(new GetChatMenuButtonRequest(321L));

        assertEquals("/bottoken/getChatMenuButton", httpClient.lastRequest().uri().getPath());
        assertTrue(result instanceof ru.tardyon.botframework.telegram.api.model.menu.MenuButtonWebApp);
    }

    private static String okTrueResponse() {
        return """
            {"ok":true,"result":true}
            """;
    }

    private static byte[] readBody(HttpRequest request) {
        HttpRequest.BodyPublisher publisher = request.bodyPublisher().orElseThrow();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                byte[] chunk = new byte[item.remaining()];
                item.get(chunk);
                out.writeBytes(chunk);
            }

            @Override
            public void onError(Throwable throwable) {
                throw new IllegalStateException(throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        return out.toByteArray();
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final byte[] responseBody;
        private HttpRequest lastRequest;

        private RecordingHttpClient(String responseBody) {
            this.responseBody = responseBody.getBytes(StandardCharsets.UTF_8);
        }

        HttpRequest lastRequest() {
            return lastRequest;
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            this.lastRequest = request;
            HttpResponse.ResponseInfo responseInfo = new HttpResponse.ResponseInfo() {
                @Override
                public int statusCode() {
                    return 200;
                }

                @Override
                public HttpHeaders headers() {
                    return HttpHeaders.of(Map.of(), (a, b) -> true);
                }

                @Override
                public Version version() {
                    return Version.HTTP_1_1;
                }
            };
            HttpResponse.BodySubscriber<T> subscriber = responseBodyHandler.apply(responseInfo);
            subscriber.onSubscribe(new Flow.Subscription() {
                private boolean done;

                @Override
                public void request(long n) {
                    if (done) {
                        return;
                    }
                    done = true;
                    subscriber.onNext(List.of(ByteBuffer.wrap(responseBody)));
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {
                    done = true;
                }
            });
            T body = subscriber.getBody().toCompletableFuture().join();
            return new StubHttpResponse<>(request, body);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
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
        public Optional<Duration> connectTimeout() {
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

    private record StubHttpResponse<T>(HttpRequest request, T body) implements HttpResponse<T> {
        @Override
        public int statusCode() {
            return 200;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(Map.of(), (a, b) -> true);
        }

        @Override
        public T body() {
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
