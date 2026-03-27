package ru.tardyon.botframework.telegram.testkit.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import ru.tardyon.botframework.telegram.testkit.update.PollingUpdateSimulator;

/**
 * Lightweight fake Telegram Bot API server for JUnit integration tests.
 */
public final class FakeBotApiServer implements AutoCloseable {

    private static final String BOT_PATH_SEGMENT = "/bot";

    private final HttpServer httpServer;
    private final ObjectMapper objectMapper;
    private final AtomicLong sequence = new AtomicLong(0);
    private final List<FakeBotApiRequest> requests = new CopyOnWriteArrayList<>();
    private final Map<String, Deque<FakeBotApiResponse>> queuedResponses = new ConcurrentHashMap<>();

    private volatile Function<FakeBotApiRequest, FakeBotApiResponse> defaultResponder;
    private volatile PollingUpdateSimulator pollingUpdateSimulator;

    public FakeBotApiServer() {
        this(0, new ObjectMapper());
    }

    public FakeBotApiServer(int port) {
        this(port, new ObjectMapper());
    }

    public FakeBotApiServer(int port, ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.defaultResponder = request -> FakeBotApiResponse.okBoolean();
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create fake Bot API server", e);
        }
        this.httpServer.createContext("/", this::handle);
    }

    public FakeBotApiServer start() {
        httpServer.start();
        return this;
    }

    public void stop() {
        httpServer.stop(0);
    }

    @Override
    public void close() {
        stop();
    }

    public int port() {
        return httpServer.getAddress().getPort();
    }

    public String baseUrl() {
        return "http://127.0.0.1:" + port();
    }

    public FakeBotApiServer attachPollingSimulator(PollingUpdateSimulator simulator) {
        this.pollingUpdateSimulator = Objects.requireNonNull(simulator, "simulator must not be null");
        return this;
    }

    public FakeBotApiServer setDefaultResponder(Function<FakeBotApiRequest, FakeBotApiResponse> responder) {
        this.defaultResponder = Objects.requireNonNull(responder, "responder must not be null");
        return this;
    }

    public FakeBotApiServer enqueueResponse(String telegramMethod, FakeBotApiResponse response) {
        String normalized = normalizeMethod(telegramMethod);
        Objects.requireNonNull(response, "response must not be null");
        queuedResponses.computeIfAbsent(normalized, ignored -> new ConcurrentLinkedDeque<>()).addLast(response);
        return this;
    }

    public FakeBotApiServer enqueueOkResult(String telegramMethod, Object result) {
        try {
            return enqueueResponse(
                telegramMethod,
                FakeBotApiResponse.okJson(objectMapper.writeValueAsString(result))
            );
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize fake result for method " + telegramMethod, e);
        }
    }

    public FakeBotApiServer clearRequests() {
        requests.clear();
        return this;
    }

    public List<FakeBotApiRequest> recordedRequests() {
        return List.copyOf(requests);
    }

    public List<FakeBotApiRequest> recordedRequests(String telegramMethod) {
        String normalized = normalizeMethod(telegramMethod);
        return requests.stream().filter(request -> request.telegramMethod().equals(normalized)).toList();
    }

    private void handle(HttpExchange exchange) throws IOException {
        FakeBotApiRequest request = readRequest(exchange);
        requests.add(request);

        FakeBotApiResponse response;
        try {
            response = resolveResponse(request);
        } catch (RuntimeException e) {
            response = FakeBotApiResponse.json(500, "{\"ok\":false,\"error_code\":500,\"description\":\"" +
                e.getClass().getSimpleName() + "\"}");
        }

        writeResponse(exchange, response);
    }

    private FakeBotApiRequest readRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String telegramMethod = extractTelegramMethod(path);
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return new FakeBotApiRequest(
            sequence.incrementAndGet(),
            exchange.getRequestMethod(),
            path,
            telegramMethod,
            body,
            copyHeaders(exchange.getRequestHeaders())
        );
    }

    private FakeBotApiResponse resolveResponse(FakeBotApiRequest request) {
        Deque<FakeBotApiResponse> methodResponses = queuedResponses.get(request.telegramMethod());
        if (methodResponses != null) {
            FakeBotApiResponse next = methodResponses.pollFirst();
            if (next != null) {
                return next;
            }
        }

        if ("getUpdates".equals(request.telegramMethod()) && pollingUpdateSimulator != null) {
            return pollingUpdateSimulator.buildGetUpdatesResponse(request.body(), objectMapper);
        }

        return defaultResponder.apply(request);
    }

    private static String extractTelegramMethod(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Request path must not be blank");
        }
        int botIndex = path.toLowerCase(Locale.ROOT).indexOf(BOT_PATH_SEGMENT);
        if (botIndex < 0) {
            throw new IllegalArgumentException("Path does not match Telegram Bot API style: " + path);
        }
        int methodSeparator = path.lastIndexOf('/');
        if (methodSeparator < 0 || methodSeparator == path.length() - 1) {
            throw new IllegalArgumentException("Cannot extract Telegram method from path: " + path);
        }
        return path.substring(methodSeparator + 1);
    }

    private static String normalizeMethod(String methodName) {
        Objects.requireNonNull(methodName, "methodName must not be null");
        if (methodName.isBlank()) {
            throw new IllegalArgumentException("methodName must not be blank");
        }
        return methodName;
    }

    private static void writeResponse(HttpExchange exchange, FakeBotApiResponse response) throws IOException {
        for (Map.Entry<String, List<String>> header : response.headers().entrySet()) {
            exchange.getResponseHeaders().put(header.getKey(), new ArrayList<>(header.getValue()));
        }

        byte[] body = response.body() == null ? new byte[0] : response.body().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(response.statusCode(), body.length);
        if (body.length > 0) {
            exchange.getResponseBody().write(body);
        }
        exchange.close();
    }

    private static Map<String, List<String>> copyHeaders(Map<String, List<String>> input) {
        Map<String, List<String>> copied = new ConcurrentHashMap<>();
        for (Map.Entry<String, List<String>> entry : input.entrySet()) {
            copied.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return Map.copyOf(copied);
    }
}
