package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReplyParameters(
    @JsonProperty("message_id") Integer messageId
) {
    public ReplyParameters {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId must not be null");
        }
    }

    public static ReplyParameters of(int messageId) {
        return new ReplyParameters(messageId);
    }
}
