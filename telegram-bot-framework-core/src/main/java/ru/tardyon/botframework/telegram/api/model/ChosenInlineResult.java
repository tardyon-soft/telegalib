package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChosenInlineResult(
    @JsonProperty("result_id") String resultId,
    User from,
    Location location,
    @JsonProperty("inline_message_id") String inlineMessageId,
    String query
) {
}
