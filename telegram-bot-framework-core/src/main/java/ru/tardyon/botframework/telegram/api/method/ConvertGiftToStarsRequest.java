package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConvertGiftToStarsRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("owned_gift_id") String ownedGiftId
) {

    public ConvertGiftToStarsRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (ownedGiftId == null || ownedGiftId.isBlank()) {
            throw new IllegalArgumentException("ownedGiftId must not be blank");
        }
    }
}
