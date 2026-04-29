package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageId(
    @JsonProperty("message_id") Integer messageId
) {
}
