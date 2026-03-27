package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpgradeGiftRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("owned_gift_id") String ownedGiftId,
    @JsonProperty("keep_original_details") Boolean keepOriginalDetails,
    @JsonProperty("star_count") Integer starCount
) {

    public UpgradeGiftRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (ownedGiftId == null || ownedGiftId.isBlank()) {
            throw new IllegalArgumentException("ownedGiftId must not be blank");
        }
        if (starCount != null && starCount < 0) {
            throw new IllegalArgumentException("starCount must be non-negative");
        }
    }
}
