package ru.tardyon.botframework.telegram.api.model.webapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SentWebAppMessage(
    @JsonProperty("inline_message_id") String inlineMessageId
) {
}
