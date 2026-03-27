package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetUserGiftsRequest(
    @JsonProperty("user_id") Long userId,
    @JsonProperty("exclude_unlimited") Boolean excludeUnlimited,
    @JsonProperty("exclude_limited_upgradable") Boolean excludeLimitedUpgradable,
    @JsonProperty("exclude_limited_non_upgradable") Boolean excludeLimitedNonUpgradable,
    @JsonProperty("exclude_from_blockchain") Boolean excludeFromBlockchain,
    @JsonProperty("exclude_unique") Boolean excludeUnique,
    @JsonProperty("sort_by_price") Boolean sortByPrice,
    String offset,
    Integer limit
) {

    public GetUserGiftsRequest {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (limit != null && (limit < 1 || limit > 100)) {
            throw new IllegalArgumentException("limit must be in range 1..100");
        }
    }
}
