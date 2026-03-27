package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message descriptor when Telegram marks callback_query.message as inaccessible.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InaccessibleMessage(
    Chat chat,
    @JsonProperty("message_id") Integer messageId,
    Integer date
) implements MaybeInaccessibleMessage {
}
