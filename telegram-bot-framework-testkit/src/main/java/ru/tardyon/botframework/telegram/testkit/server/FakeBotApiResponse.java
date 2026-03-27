package ru.tardyon.botframework.telegram.testkit.server;

import java.util.List;
import java.util.Map;

/**
 * HTTP response model returned by {@link FakeBotApiServer}.
 */
public record FakeBotApiResponse(
    int statusCode,
    String body,
    Map<String, List<String>> headers
) {

    private static final Map<String, List<String>> JSON_HEADERS =
        Map.of("Content-Type", List.of("application/json; charset=UTF-8"));

    public FakeBotApiResponse {
        headers = headers == null ? Map.of() : headers;
    }

    public static FakeBotApiResponse json(int statusCode, String body) {
        return new FakeBotApiResponse(statusCode, body, JSON_HEADERS);
    }

    public static FakeBotApiResponse okJson(String resultJson) {
        return json(200, "{\"ok\":true,\"result\":" + resultJson + "}");
    }

    public static FakeBotApiResponse okBoolean() {
        return okJson("true");
    }

    public static FakeBotApiResponse telegramError(int errorCode, String description) {
        return json(200, "{\"ok\":false,\"error_code\":" + errorCode + ",\"description\":\"" + escape(description) + "\"}");
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
