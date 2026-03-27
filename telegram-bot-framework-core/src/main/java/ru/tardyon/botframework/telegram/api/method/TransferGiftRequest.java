package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferGiftRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("owned_gift_id") String ownedGiftId,
    @JsonProperty("new_owner_chat_id") Long newOwnerChatId,
    @JsonProperty("star_count") Integer starCount
) {

    public TransferGiftRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (ownedGiftId == null || ownedGiftId.isBlank()) {
            throw new IllegalArgumentException("ownedGiftId must not be blank");
        }
        if (newOwnerChatId == null) {
            throw new IllegalArgumentException("newOwnerChatId must not be null");
        }
        if (starCount != null && starCount < 0) {
            throw new IllegalArgumentException("starCount must be non-negative");
        }
    }
}
