package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record AnswerCallbackQueryRequest(
    @JsonProperty("callback_query_id") String callbackQueryId,
    String text,
    @JsonProperty("show_alert") Boolean showAlert,
    String url,
    @JsonProperty("cache_time") Integer cacheTime
) {
    public AnswerCallbackQueryRequest {
        Objects.requireNonNull(callbackQueryId, "callbackQueryId must not be null");
    }

    public static AnswerCallbackQueryRequest of(String callbackQueryId) {
        return new AnswerCallbackQueryRequest(callbackQueryId, null, null, null, null);
    }
}
