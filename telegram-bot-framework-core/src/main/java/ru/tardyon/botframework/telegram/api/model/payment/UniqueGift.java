package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import ru.tardyon.botframework.telegram.api.model.Chat;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UniqueGift(
    @JsonProperty("gift_id") String giftId,
    @JsonProperty("base_name") String baseName,
    String name,
    Integer number,
    JsonNode model,
    JsonNode symbol,
    JsonNode backdrop,
    @JsonProperty("is_premium") Boolean isPremium,
    @JsonProperty("is_burned") Boolean isBurned,
    @JsonProperty("is_from_blockchain") Boolean isFromBlockchain,
    JsonNode colors,
    @JsonProperty("publisher_chat") Chat publisherChat
) {
}
