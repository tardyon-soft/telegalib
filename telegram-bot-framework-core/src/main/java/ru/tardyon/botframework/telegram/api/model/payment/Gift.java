package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import ru.tardyon.botframework.telegram.api.model.Chat;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Gift(
    String id,
    JsonNode sticker,
    @JsonProperty("star_count") Integer starCount,
    @JsonProperty("upgrade_star_count") Integer upgradeStarCount,
    @JsonProperty("is_premium") Boolean isPremium,
    @JsonProperty("has_colors") Boolean hasColors,
    @JsonProperty("total_count") Integer totalCount,
    @JsonProperty("remaining_count") Integer remainingCount,
    @JsonProperty("personal_total_count") Integer personalTotalCount,
    @JsonProperty("personal_remaining_count") Integer personalRemainingCount,
    GiftBackground background,
    @JsonProperty("unique_gift_variant_count") Integer uniqueGiftVariantCount,
    @JsonProperty("publisher_chat") Chat publisherChat
) {
}
