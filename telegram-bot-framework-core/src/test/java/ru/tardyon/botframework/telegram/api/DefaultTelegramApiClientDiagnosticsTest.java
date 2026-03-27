package ru.tardyon.botframework.telegram.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.BiPredicate;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.method.DeleteMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.transport.profile.BotApiTransportProfile;
import ru.tardyon.botframework.telegram.diagnostics.BotApiRequestEvent;
import ru.tardyon.botframework.telegram.diagnostics.BotApiResponseEvent;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticErrorEvent;
import ru.tardyon.botframework.telegram.diagnostics.DiagnosticsHooks;
import ru.tardyon.botframework.telegram.exception.TelegramApiException;

class DefaultTelegramApiClientDiagnosticsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void publishesApiTimingAndRedactedRequestEvents() {
        List<BotApiRequestEvent> requests = new ArrayList<>();
        List<BotApiResponseEvent> responses = new ArrayList<>();

        DiagnosticsHooks hooks = DiagnosticsHooks.builder()
            .addRequestListener(requests::add)
            .addResponseListener(responses::add)
            .build();

        RecordingHttpClient httpClient = new RecordingHttpClient("{\"ok\":true,\"result\":true}");
        DefaultTelegramApiClient client = new DefaultTelegramApiClient(
            "123456:ABCDEF",
            BotApiTransportProfile.cloudDefault(),
            httpClient,
            objectMapper,
            hooks
        );

        client.setWebhook(new SetWebhookRequest("https://example.com/webhook", null, null, null, null, "secret-token-1"));

        assertEquals(1, requests.size());
        assertEquals(1, responses.size());

        BotApiRequestEvent requestEvent = requests.getFirst();
        BotApiResponseEvent responseEvent = responses.getFirst();

        assertEquals("setWebhook", requestEvent.methodName());
        assertNotNull(requestEvent.correlationId());
        assertFalse(requestEvent.correlationId().isBlank());
        assertTrue(requestEvent.redactedRequestBody().contains("<redacted>"));
        assertFalse(requestEvent.redactedRequestBody().contains("secret-token-1"));

        assertEquals(requestEvent.correlationId(), responseEvent.correlationId());
        assertEquals("setWebhook", responseEvent.methodName());
        assertTrue(responseEvent.success());
        assertTrue(responseEvent.durationMillis() >= 0);
        assertEquals(200, responseEvent.httpStatus());
    }

    @Test
    void publishesFailureAndErrorEventsOnTelegramErrorEnvelope() {
        List<BotApiResponseEvent> responses = new ArrayList<>();
        List<DiagnosticErrorEvent> errors = new ArrayList<>();

        DiagnosticsHooks hooks = DiagnosticsHooks.builder()
            .addResponseListener(responses::add)
            .addErrorListener(errors::add)
            .build();

        RecordingHttpClient httpClient = new RecordingHttpClient("{\"ok\":false,\"error_code\":400,\"description\":\"Bad Request\"}");
        DefaultTelegramApiClient client = new DefaultTelegramApiClient(
            "123456:ABCDEF",
            BotApiTransportProfile.cloudDefault(),
            httpClient,
            objectMapper,
            hooks
        );

        try {
            client.deleteMessage(new DeleteMessageRequest(1L, 10));
        } catch (TelegramApiException expected) {
            // expected
        }

        assertEquals(1, responses.size());
        assertEquals(1, errors.size());

        BotApiResponseEvent responseEvent = responses.getFirst();
        assertFalse(responseEvent.success());
        assertEquals(400, responseEvent.telegramErrorCode());
        assertEquals("Bad Request", responseEvent.telegramDescription());

        DiagnosticErrorEvent errorEvent = errors.getFirst();
        assertEquals("api-client", errorEvent.component());
        assertEquals("deleteMessage", errorEvent.methodName());
        assertNotNull(errorEvent.error());
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final byte[] defaultJsonBody;

        private RecordingHttpClient(String defaultJsonBody) {
            this.defaultJsonBody = defaultJsonBody.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            return response(request, responseBodyHandler, defaultJsonBody, 200);
        }

        private <T> HttpResponse<T> response(
            HttpRequest request,
            HttpResponse.BodyHandler<T> bodyHandler,
            byte[] body,
            int statusCode
        ) {
            HttpResponse.ResponseInfo responseInfo = new HttpResponse.ResponseInfo() {
                @Override
                public int statusCode() {
                    return statusCode;
                }

                @Override
                public HttpHeaders headers() {
                    return HttpHeaders.of(Map.of(), ALWAYS_TRUE);
                }

                @Override
                public HttpClient.Version version() {
                    return HttpClient.Version.HTTP_1_1;
                }
            };
            HttpResponse.BodySubscriber<T> subscriber = bodyHandler.apply(responseInfo);
            subscriber.onSubscribe(new Flow.Subscription() {
                private boolean done;

                @Override
                public void request(long n) {
                    if (done) {
                        return;
                    }
                    done = true;
                    subscriber.onNext(List.of(ByteBuffer.wrap(body)));
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {
                    done = true;
                }
            });
            T decodedBody = subscriber.getBody().toCompletableFuture().join();
            return new StubHttpResponse<>(request, statusCode, decodedBody);
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

        private static final BiPredicate<String, String> ALWAYS_TRUE = (a, b) -> true;
    }

    private record StubHttpResponse<T>(HttpRequest request, int statusCode, T body) implements HttpResponse<T> {
        @Override
        public int statusCode() {
            return statusCode;
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
            return HttpHeaders.of(Map.of(), (name, value) -> true);
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
