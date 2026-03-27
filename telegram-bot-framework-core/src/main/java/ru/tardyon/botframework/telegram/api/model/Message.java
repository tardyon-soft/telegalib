package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
    @JsonProperty("message_id") Integer messageId,
    User from,
    Chat chat,
    Integer date,
    String text,
    List<MessageEntity> entities,
    @JsonProperty("edit_date") Integer editDate,
    @JsonProperty("reply_to_message") Message replyToMessage
) implements MaybeInaccessibleMessage {
}
