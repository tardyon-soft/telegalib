package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.payment.Invoice;
import ru.tardyon.botframework.telegram.api.model.payment.PaidMediaInfo;
import ru.tardyon.botframework.telegram.api.model.payment.PaidMediaPurchased;
import ru.tardyon.botframework.telegram.api.model.payment.RefundedPayment;
import ru.tardyon.botframework.telegram.api.model.payment.SuccessfulPayment;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppData;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("message_id") Integer messageId,
    User from,
    @JsonProperty("sender_business_bot") User senderBusinessBot,
    Chat chat,
    Integer date,
    String text,
    List<MessageEntity> entities,
    @JsonProperty("edit_date") Integer editDate,
    @JsonProperty("reply_to_message") Message replyToMessage,
    Invoice invoice,
    @JsonProperty("paid_media") PaidMediaInfo paidMedia,
    @JsonProperty("paid_media_purchased") PaidMediaPurchased paidMediaPurchased,
    @JsonProperty("successful_payment") SuccessfulPayment successfulPayment,
    @JsonProperty("refunded_payment") RefundedPayment refundedPayment,
    @JsonProperty("web_app_data") WebAppData webAppData
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
        this(null, messageId, from, null, chat, date, text, entities, editDate, replyToMessage, null, null, null, null, null, null);
    }

    public Message(
        Integer messageId,
        User from,
        Chat chat,
        Integer date,
        String text,
        List<MessageEntity> entities,
        Integer editDate,
        Message replyToMessage,
        Invoice invoice,
        SuccessfulPayment successfulPayment
    ) {
        this(null, messageId, from, null, chat, date, text, entities, editDate, replyToMessage, invoice, null, null, successfulPayment, null, null);
    }

    public Message(
        String businessConnectionId,
        Integer messageId,
        User from,
        User senderBusinessBot,
        Chat chat,
        Integer date,
        String text,
        List<MessageEntity> entities,
        Integer editDate,
        Message replyToMessage,
        Invoice invoice,
        SuccessfulPayment successfulPayment,
        WebAppData webAppData
    ) {
        this(
            businessConnectionId,
            messageId,
            from,
            senderBusinessBot,
            chat,
            date,
            text,
            entities,
            editDate,
            replyToMessage,
            invoice,
            null,
            null,
            successfulPayment,
            null,
            webAppData
        );
    }

    public Message(
        String businessConnectionId,
        Integer messageId,
        User from,
        User senderBusinessBot,
        Chat chat,
        Integer date,
        String text,
        List<MessageEntity> entities,
        Integer editDate,
        Message replyToMessage
    ) {
        this(
            businessConnectionId,
            messageId,
            from,
            senderBusinessBot,
            chat,
            date,
            text,
            entities,
            editDate,
            replyToMessage,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }
}
