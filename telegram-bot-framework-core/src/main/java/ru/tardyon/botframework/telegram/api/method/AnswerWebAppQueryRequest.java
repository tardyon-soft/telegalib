package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;

public record AnswerWebAppQueryRequest(
    @JsonProperty("web_app_query_id") String webAppQueryId,
    InlineQueryResult result
) {

    public AnswerWebAppQueryRequest {
        Objects.requireNonNull(webAppQueryId, "webAppQueryId must not be null");
        if (webAppQueryId.isBlank()) {
            throw new IllegalArgumentException("webAppQueryId must not be blank");
        }
        Objects.requireNonNull(result, "result must not be null");
    }
}
