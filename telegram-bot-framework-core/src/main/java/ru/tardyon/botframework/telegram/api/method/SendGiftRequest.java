package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record SendGiftRequest(
    @JsonProperty("user_id") Long userId,
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("gift_id") String giftId,
    @JsonProperty("pay_for_upgrade") Boolean payForUpgrade,
    String text,
    @JsonProperty("text_parse_mode") String textParseMode,
    @JsonProperty("text_entities") List<MessageEntity> textEntities
) {

    public SendGiftRequest {
        if (userId == null && chatId == null) {
            throw new IllegalArgumentException("Either userId or chatId must be specified");
        }
        Objects.requireNonNull(giftId, "giftId must not be null");
        if (giftId.isBlank()) {
            throw new IllegalArgumentException("giftId must not be blank");
        }
        if (text != null && text.length() > 128) {
            throw new IllegalArgumentException("text length must be in range 0..128");
        }
        textEntities = textEntities == null ? null : List.copyOf(textEntities);
    }
}
