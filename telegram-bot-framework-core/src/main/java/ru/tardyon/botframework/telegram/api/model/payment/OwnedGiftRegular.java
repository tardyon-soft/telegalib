package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OwnedGiftRegular(
    String type,
    Gift gift,
    @JsonProperty("owned_gift_id") String ownedGiftId,
    @JsonProperty("sender_user") User senderUser,
    @JsonProperty("send_date") Integer sendDate,
    String text,
    List<MessageEntity> entities,
    @JsonProperty("is_private") Boolean isPrivate,
    @JsonProperty("is_saved") Boolean isSaved,
    @JsonProperty("can_be_upgraded") Boolean canBeUpgraded,
    @JsonProperty("was_refunded") Boolean wasRefunded,
    @JsonProperty("convert_star_count") Integer convertStarCount,
    @JsonProperty("prepaid_upgrade_star_count") Integer prepaidUpgradeStarCount,
    @JsonProperty("is_upgrade_separate") Boolean isUpgradeSeparate,
    @JsonProperty("unique_gift_number") Integer uniqueGiftNumber
) implements OwnedGift {

    public OwnedGiftRegular {
        entities = entities == null ? null : List.copyOf(entities);
    }
}
