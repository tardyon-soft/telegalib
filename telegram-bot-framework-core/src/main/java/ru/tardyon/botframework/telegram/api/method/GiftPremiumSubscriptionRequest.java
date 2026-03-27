package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record GiftPremiumSubscriptionRequest(
    @JsonProperty("user_id") Long userId,
    @JsonProperty("month_count") Integer monthCount,
    @JsonProperty("star_count") Integer starCount,
    String text,
    @JsonProperty("text_parse_mode") String textParseMode,
    @JsonProperty("text_entities") List<MessageEntity> textEntities
) {

    public GiftPremiumSubscriptionRequest {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (monthCount == null) {
            throw new IllegalArgumentException("monthCount must not be null");
        }
        if (starCount == null) {
            throw new IllegalArgumentException("starCount must not be null");
        }

        int expectedStars = switch (monthCount) {
            case 3 -> 1000;
            case 6 -> 1500;
            case 12 -> 2500;
            default -> throw new IllegalArgumentException("monthCount must be one of 3, 6, or 12");
        };

        if (starCount != expectedStars) {
            throw new IllegalArgumentException("starCount does not match monthCount requirements");
        }

        if (text != null && text.length() > 128) {
            throw new IllegalArgumentException("text length must be in range 0..128");
        }
        textEntities = textEntities == null ? null : List.copyOf(textEntities);
    }
}
