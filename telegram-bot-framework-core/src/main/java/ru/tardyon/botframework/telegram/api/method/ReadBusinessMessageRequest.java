package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record ReadBusinessMessageRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Long chatId,
    @JsonProperty("message_id") Integer messageId
) {

    public ReadBusinessMessageRequest {
        Objects.requireNonNull(businessConnectionId, "businessConnectionId must not be null");
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(messageId, "messageId must not be null");
        if (businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
    }
}
