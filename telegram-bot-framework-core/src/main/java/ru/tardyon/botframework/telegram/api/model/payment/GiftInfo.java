package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GiftInfo(
    Gift gift,
    @JsonProperty("owned_gift_id") String ownedGiftId,
    @JsonProperty("convert_star_count") Integer convertStarCount,
    @JsonProperty("prepaid_upgrade_star_count") Integer prepaidUpgradeStarCount,
    @JsonProperty("is_upgrade_separate") Boolean isUpgradeSeparate,
    @JsonProperty("can_be_upgraded") Boolean canBeUpgraded,
    String text,
    List<MessageEntity> entities,
    @JsonProperty("is_private") Boolean isPrivate,
    @JsonProperty("unique_gift_number") Integer uniqueGiftNumber
) {

    public GiftInfo {
        entities = entities == null ? null : List.copyOf(entities);
    }
}
