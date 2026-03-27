package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetStarTransactionsRequest(
    String offset,
    @JsonProperty("limit") Integer limit
) {

    public GetStarTransactionsRequest {
        if (limit != null && (limit < 1 || limit > 100)) {
            throw new IllegalArgumentException("limit must be in range 1..100");
        }
    }
}

