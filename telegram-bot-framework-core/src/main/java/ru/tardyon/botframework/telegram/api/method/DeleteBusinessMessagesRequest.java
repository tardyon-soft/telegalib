package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public record DeleteBusinessMessagesRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("message_ids") List<Integer> messageIds
) {

    public DeleteBusinessMessagesRequest {
        Objects.requireNonNull(businessConnectionId, "businessConnectionId must not be null");
        Objects.requireNonNull(messageIds, "messageIds must not be null");
        if (businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (messageIds.isEmpty()) {
            throw new IllegalArgumentException("messageIds must not be empty");
        }
        messageIds = List.copyOf(messageIds);
    }
}
