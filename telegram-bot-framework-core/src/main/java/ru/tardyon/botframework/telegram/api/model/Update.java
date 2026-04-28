package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.business.BusinessMessagesDeleted;
import ru.tardyon.botframework.telegram.api.model.chatmember.ChatMemberUpdated;
import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingQuery;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Update(
    @JsonProperty("update_id") Long updateId,
    Message message,
    @JsonProperty("edited_message") Message editedMessage,
    @JsonProperty("channel_post") Message channelPost,
    @JsonProperty("edited_channel_post") Message editedChannelPost,
    @JsonProperty("callback_query") CallbackQuery callbackQuery,
    @JsonProperty("shipping_query") ShippingQuery shippingQuery,
    @JsonProperty("pre_checkout_query") PreCheckoutQuery preCheckoutQuery,
    @JsonProperty("business_connection") BusinessConnection businessConnection,
    @JsonProperty("business_message") Message businessMessage,
    @JsonProperty("edited_business_message") Message editedBusinessMessage,
    @JsonProperty("deleted_business_messages") BusinessMessagesDeleted deletedBusinessMessages,
    @JsonProperty("inline_query") InlineQuery inlineQuery,
    @JsonProperty("chosen_inline_result") ChosenInlineResult chosenInlineResult,
    @JsonProperty("my_chat_member") ChatMemberUpdated myChatMember,
    @JsonProperty("chat_member") ChatMemberUpdated chatMember
) {

    public Update(
        Long updateId,
        Message message,
        Message editedMessage,
        Message channelPost,
        Message editedChannelPost,
        CallbackQuery callbackQuery,
        ShippingQuery shippingQuery,
        PreCheckoutQuery preCheckoutQuery,
        BusinessConnection businessConnection,
        Message businessMessage,
        Message editedBusinessMessage,
        BusinessMessagesDeleted deletedBusinessMessages,
        InlineQuery inlineQuery,
        ChosenInlineResult chosenInlineResult
    ) {
        this(
            updateId,
            message,
            editedMessage,
            channelPost,
            editedChannelPost,
            callbackQuery,
            shippingQuery,
            preCheckoutQuery,
            businessConnection,
            businessMessage,
            editedBusinessMessage,
            deletedBusinessMessages,
            inlineQuery,
            chosenInlineResult,
            null,
            null
        );
    }

    public Update(
        Long updateId,
        Message message,
        Message editedMessage,
        Message channelPost,
        Message editedChannelPost,
        CallbackQuery callbackQuery,
        InlineQuery inlineQuery,
        ChosenInlineResult chosenInlineResult
    ) {
        this(
            updateId,
            message,
            editedMessage,
            channelPost,
            editedChannelPost,
            callbackQuery,
            null,
            null,
            null,
            null,
            null,
            null,
            inlineQuery,
            chosenInlineResult,
            null,
            null
        );
    }

    public Update(
        Long updateId,
        Message message,
        Message editedMessage,
        Message channelPost,
        Message editedChannelPost,
        CallbackQuery callbackQuery,
        ShippingQuery shippingQuery,
        PreCheckoutQuery preCheckoutQuery,
        InlineQuery inlineQuery,
        ChosenInlineResult chosenInlineResult
    ) {
        this(
            updateId,
            message,
            editedMessage,
            channelPost,
            editedChannelPost,
            callbackQuery,
            shippingQuery,
            preCheckoutQuery,
            null,
            null,
            null,
            null,
            inlineQuery,
            chosenInlineResult,
            null,
            null
        );
    }
}
