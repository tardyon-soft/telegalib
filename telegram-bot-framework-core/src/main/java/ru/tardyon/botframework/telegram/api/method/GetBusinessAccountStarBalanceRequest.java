package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetBusinessAccountStarBalanceRequest(
    @JsonProperty("business_connection_id") String businessConnectionId
) {

    public GetBusinessAccountStarBalanceRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
    }
}
