package ru.tardyon.botframework.telegram.webhook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Optional minimal JDK HttpServer adapter for webhook update delivery into core dispatcher pipeline.
 */
public class JdkWebhookHttpServer {

    private static final byte[] OK_BODY = "OK".getBytes(StandardCharsets.UTF_8);

    private final HttpServer httpServer;

    public JdkWebhookHttpServer(int port, String path, WebhookUpdateProcessor webhookUpdateProcessor) throws IOException {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(webhookUpdateProcessor, "webhookUpdateProcessor must not be null");
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.createContext(path, new WebhookHandler(webhookUpdateProcessor));
    }

    public void start() {
        httpServer.start();
    }

    public void stop(int delaySeconds) {
        httpServer.stop(delaySeconds);
    }

    private static final class WebhookHandler implements HttpHandler {

        private final WebhookUpdateProcessor webhookUpdateProcessor;

        private WebhookHandler(WebhookUpdateProcessor webhookUpdateProcessor) {
            this.webhookUpdateProcessor = webhookUpdateProcessor;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, List<String>> headers = exchange.getRequestHeaders();
                webhookUpdateProcessor.process(requestBody, new WebhookRequestMetadata(headers));

                exchange.sendResponseHeaders(200, OK_BODY.length);
                exchange.getResponseBody().write(OK_BODY);
            } catch (WebhookSecurityException e) {
                exchange.sendResponseHeaders(403, -1);
            } catch (IllegalArgumentException e) {
                exchange.sendResponseHeaders(400, -1);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
            } finally {
                exchange.close();
            }
        }
    }
}
