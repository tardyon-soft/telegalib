package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferBusinessAccountStarsRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("star_count") Integer starCount
) {

    public TransferBusinessAccountStarsRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (starCount == null) {
            throw new IllegalArgumentException("starCount must not be null");
        }
        if (starCount < 1 || starCount > 10000) {
            throw new IllegalArgumentException("starCount must be in range 1..10000");
        }
    }
}
