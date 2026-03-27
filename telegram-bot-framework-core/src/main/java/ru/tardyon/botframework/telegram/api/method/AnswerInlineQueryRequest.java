package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultsButton;

public record AnswerInlineQueryRequest(
    @JsonProperty("inline_query_id") String inlineQueryId,
    List<InlineQueryResult> results,
    @JsonProperty("cache_time") Integer cacheTime,
    @JsonProperty("is_personal") Boolean isPersonal,
    @JsonProperty("next_offset") String nextOffset,
    InlineQueryResultsButton button
) {

    public AnswerInlineQueryRequest {
        Objects.requireNonNull(inlineQueryId, "inlineQueryId must not be null");
        if (inlineQueryId.isBlank()) {
            throw new IllegalArgumentException("inlineQueryId must not be blank");
        }
        Objects.requireNonNull(results, "results must not be null");
        if (results.size() > 50) {
            throw new IllegalArgumentException("results size must be <= 50");
        }
        if (nextOffset != null && nextOffset.getBytes(StandardCharsets.UTF_8).length > 64) {
            throw new IllegalArgumentException("nextOffset must be <= 64 bytes");
        }
        results = List.copyOf(results);
    }
}
