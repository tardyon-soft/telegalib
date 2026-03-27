package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.payment.Invoice;
import ru.tardyon.botframework.telegram.api.model.payment.SuccessfulPayment;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
    @JsonProperty("message_id") Integer messageId,
    User from,
    Chat chat,
    Integer date,
    String text,
    List<MessageEntity> entities,
    @JsonProperty("edit_date") Integer editDate,
    @JsonProperty("reply_to_message") Message replyToMessage,
    Invoice invoice,
    @JsonProperty("successful_payment") SuccessfulPayment successfulPayment
) implements MaybeInaccessibleMessage {

    public Message(
        Integer messageId,
        User from,
        Chat chat,
        Integer date,
        String text,
        List<MessageEntity> entities,
        Integer editDate,
        Message replyToMessage
    ) {
        this(messageId, from, chat, date, text, entities, editDate, replyToMessage, null, null);
    }
}
