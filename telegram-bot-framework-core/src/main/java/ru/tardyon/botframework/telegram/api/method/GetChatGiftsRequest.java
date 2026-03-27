package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetChatGiftsRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("exclude_unsaved") Boolean excludeUnsaved,
    @JsonProperty("exclude_saved") Boolean excludeSaved,
    @JsonProperty("exclude_unlimited") Boolean excludeUnlimited,
    @JsonProperty("exclude_limited_upgradable") Boolean excludeLimitedUpgradable,
    @JsonProperty("exclude_limited_non_upgradable") Boolean excludeLimitedNonUpgradable,
    @JsonProperty("exclude_from_blockchain") Boolean excludeFromBlockchain,
    @JsonProperty("exclude_unique") Boolean excludeUnique,
    @JsonProperty("sort_by_price") Boolean sortByPrice,
    String offset,
    Integer limit
) {

    public GetChatGiftsRequest {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId must not be null");
        }
        if (limit != null && (limit < 1 || limit > 100)) {
            throw new IllegalArgumentException("limit must be in range 1..100");
        }
    }
}
