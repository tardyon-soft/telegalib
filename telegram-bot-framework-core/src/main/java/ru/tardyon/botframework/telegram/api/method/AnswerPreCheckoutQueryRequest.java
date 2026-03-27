package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record AnswerPreCheckoutQueryRequest(
    @JsonProperty("pre_checkout_query_id") String preCheckoutQueryId,
    Boolean ok,
    @JsonProperty("error_message") String errorMessage
) {

    public AnswerPreCheckoutQueryRequest {
        Objects.requireNonNull(preCheckoutQueryId, "preCheckoutQueryId must not be null");
        Objects.requireNonNull(ok, "ok must not be null");

        if (Boolean.TRUE.equals(ok)) {
            if (errorMessage != null && !errorMessage.isBlank()) {
                throw new IllegalArgumentException("errorMessage must be null when ok=true");
            }
        } else {
            if (errorMessage == null || errorMessage.isBlank()) {
                throw new IllegalArgumentException("errorMessage must not be blank when ok=false");
            }
        }
    }
}
