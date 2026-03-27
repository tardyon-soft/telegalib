package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AcceptedGiftTypes(
    @JsonProperty("unlimited_gifts") Boolean unlimitedGifts,
    @JsonProperty("limited_gifts") Boolean limitedGifts,
    @JsonProperty("unique_gifts") Boolean uniqueGifts,
    @JsonProperty("premium_subscription") Boolean premiumSubscription,
    @JsonProperty("gifts_from_channels") Boolean giftsFromChannels
) {
}
