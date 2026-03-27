package ru.tardyon.botframework.telegram.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.BiPredicate;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.method.GetFileRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SendMediaGroupRequest;
import ru.tardyon.botframework.telegram.api.model.TelegramFile;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaDocument;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaPhoto;

class DefaultTelegramApiClientFileSupportTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsesGetFileResponse() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {
                  "ok": true,
                  "result": {
                    "file_id": "AAQAA",
                    "file_unique_id": "file-uid",
                    "file_size": 1234,
                    "file_path": "documents/report.pdf"
                  }
                }
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        TelegramFile file = client.getFile(new GetFileRequest("AAQAA"));

        assertEquals("AAQAA", file.fileId());
        assertEquals("file-uid", file.fileUniqueId());
        assertEquals(1234L, file.fileSize());
        assertEquals("documents/report.pdf", file.filePath());
    }

    @Test
    void sendDocumentByFileIdUsesJsonField() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okMessageResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendDocument(SendDocumentRequest.of(123L, InputFile.fileId("file-id-1")));

        HttpRequest request = httpClient.lastRequest();
        String contentType = request.headers().firstValue("Content-Type").orElse("");
        assertTrue(contentType.startsWith("application/json"));
        String jsonBody = new String(readBody(request), StandardCharsets.UTF_8);
        assertTrue(jsonBody.contains("\"document\":\"file-id-1\""));
        assertFalse(jsonBody.contains("referenceType"));
        assertFalse(jsonBody.contains("\"document\":{"));
    }

    @Test
    void sendDocumentByUploadUsesMultipartFormData() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okMessageResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        SendDocumentRequest request = new SendDocumentRequest(
            123L,
            InputFile.bytes("hello.txt", "hello".getBytes(StandardCharsets.UTF_8)),
            "test-caption",
            null
        );
        client.sendDocument(request);

        HttpRequest sentRequest = httpClient.lastRequest();
        String contentType = sentRequest.headers().firstValue("Content-Type").orElse("");
        assertTrue(contentType.startsWith("multipart/form-data; boundary="));
        String body = new String(readBody(sentRequest), StandardCharsets.UTF_8);
        assertTrue(body.contains("name=\"chat_id\""));
        assertTrue(body.contains("name=\"caption\""));
        assertTrue(body.contains("name=\"document\"; filename=\"hello.txt\""));
        assertTrue(body.contains("hello"));
    }

    @Test
    void sendMediaGroupByFileIdsUsesJson() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okMessageArrayResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendMediaGroup(
            SendMediaGroupRequest.of(
                123L,
                List.of(
                    new InputMediaPhoto(InputFile.fileId("photo-id-1")),
                    new InputMediaPhoto(InputFile.fileId("photo-id-2"))
                )
            )
        );

        HttpRequest request = httpClient.lastRequest();
        String contentType = request.headers().firstValue("Content-Type").orElse("");
        assertTrue(contentType.startsWith("application/json"));
        String jsonBody = new String(readBody(request), StandardCharsets.UTF_8);
        assertTrue(jsonBody.contains("\"media\":["));
        assertTrue(jsonBody.contains("\"type\":\"photo\""));
        assertTrue(jsonBody.contains("\"media\":\"photo-id-1\""));
        assertFalse(jsonBody.contains("attach://"));
    }

    @Test
    void sendMediaGroupWithUploadUsesMultipartAndAttachSyntax() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okMessageArrayResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendMediaGroup(
            SendMediaGroupRequest.of(
                123L,
                List.of(
                    new InputMediaDocument(InputFile.bytes("doc1.txt", "doc1".getBytes(StandardCharsets.UTF_8))),
                    new InputMediaDocument(InputFile.fileId("existing-doc-id"))
                )
            )
        );

        HttpRequest request = httpClient.lastRequest();
        String contentType = request.headers().firstValue("Content-Type").orElse("");
        assertTrue(contentType.startsWith("multipart/form-data; boundary="));
        String body = new String(readBody(request), StandardCharsets.UTF_8);
        assertTrue(body.contains("name=\"chat_id\""));
        assertTrue(body.contains("name=\"media\""));
        assertTrue(body.contains("\"media\":\"attach://media1\""));
        assertTrue(body.contains("\"media\":\"existing-doc-id\""));
        assertTrue(body.contains("name=\"media1\"; filename=\"doc1.txt\""));
    }

    @Test
    void buildsDownloadUrlAndDownloadsFile() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okMessageResponse());
        httpClient.stubDownload("/file/bottoken/documents/report.pdf", "binary-content".getBytes(StandardCharsets.UTF_8));
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        String url = client.buildFileDownloadUrl("documents/report.pdf");
        byte[] content = client.downloadFile("documents/report.pdf");

        assertEquals("https://api.telegram.org/file/bottoken/documents/report.pdf", url);
        assertArrayEquals("binary-content".getBytes(StandardCharsets.UTF_8), content);
    }

    @Test
    void downloadsFileToPath(@TempDir Path tempDir) throws Exception {
        RecordingHttpClient httpClient = new RecordingHttpClient(okMessageResponse());
        httpClient.stubDownload("/file/bottoken/files/archive.zip", "zip-bytes".getBytes(StandardCharsets.UTF_8));
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        Path target = tempDir.resolve("nested/archive.zip");
        Path savedPath = client.downloadFile("files/archive.zip", target);

        assertEquals(target, savedPath);
        assertArrayEquals("zip-bytes".getBytes(StandardCharsets.UTF_8), Files.readAllBytes(target));
    }

    private static String okMessageResponse() {
        return """
            {
              "ok": true,
              "result": {
                "message_id": 1,
                "chat": {
                  "id": 123,
                  "type": "private"
                },
                "date": 1710000000
              }
            }
            """;
    }

    private static String okMessageArrayResponse() {
        return """
            {
              "ok": true,
              "result": [
                {
                  "message_id": 1,
                  "chat": {
                    "id": 123,
                    "type": "private"
                  },
                  "date": 1710000000
                },
                {
                  "message_id": 2,
                  "chat": {
                    "id": 123,
                    "type": "private"
                  },
                  "date": 1710000001
                }
              ]
            }
            """;
    }

    private static byte[] readBody(HttpRequest request) {
        HttpRequest.BodyPublisher publisher = request.bodyPublisher().orElseThrow();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                byte[] bytes = new byte[item.remaining()];
                item.get(bytes);
                output.writeBytes(bytes);
            }

            @Override
            public void onError(Throwable throwable) {
                throw new IllegalStateException("Body publisher emitted an error", throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        return output.toByteArray();
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final byte[] defaultJsonBody;
        private final Map<String, byte[]> downloadBodies = new java.util.HashMap<>();
        private HttpRequest lastRequest;

        private RecordingHttpClient(String defaultJsonBody) {
            this.defaultJsonBody = defaultJsonBody.getBytes(StandardCharsets.UTF_8);
        }

        void stubDownload(String path, byte[] body) {
            downloadBodies.put(path, body);
        }

        HttpRequest lastRequest() {
            return lastRequest;
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            this.lastRequest = request;
            byte[] body = request.uri().getPath().startsWith("/file/")
                ? downloadBodies.getOrDefault(request.uri().getPath(), new byte[0])
                : defaultJsonBody;
            return response(request, responseBodyHandler, body, 200);
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
