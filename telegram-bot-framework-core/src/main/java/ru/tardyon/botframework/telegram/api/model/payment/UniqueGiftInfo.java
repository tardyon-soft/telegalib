package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UniqueGiftInfo(
    UniqueGift gift,
    String origin,
    @JsonProperty("last_resale_currency") String lastResaleCurrency,
    @JsonProperty("last_resale_amount") Integer lastResaleAmount,
    @JsonProperty("owned_gift_id") String ownedGiftId,
    @JsonProperty("transfer_star_count") Integer transferStarCount,
    @JsonProperty("next_transfer_date") Integer nextTransferDate
) {
}
