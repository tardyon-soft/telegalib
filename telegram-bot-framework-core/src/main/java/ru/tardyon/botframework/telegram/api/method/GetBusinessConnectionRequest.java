package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record GetBusinessConnectionRequest(
    @JsonProperty("business_connection_id") String businessConnectionId
) {

    public GetBusinessConnectionRequest {
        Objects.requireNonNull(businessConnectionId, "businessConnectionId must not be null");
        if (businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
    }
}
