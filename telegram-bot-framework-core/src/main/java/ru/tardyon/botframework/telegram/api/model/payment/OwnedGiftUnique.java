package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OwnedGiftUnique(
    String type,
    @JsonProperty("gift") UniqueGift gift,
    @JsonProperty("owned_gift_id") String ownedGiftId,
    @JsonProperty("sender_user") User senderUser,
    @JsonProperty("send_date") Integer sendDate,
    @JsonProperty("is_saved") Boolean isSaved,
    @JsonProperty("can_be_transferred") Boolean canBeTransferred,
    @JsonProperty("transfer_star_count") Integer transferStarCount,
    @JsonProperty("next_transfer_date") Integer nextTransferDate
) implements OwnedGift {
}
