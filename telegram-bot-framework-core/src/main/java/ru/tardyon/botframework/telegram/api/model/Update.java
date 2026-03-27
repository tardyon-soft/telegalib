package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("inline_query") InlineQuery inlineQuery,
    @JsonProperty("chosen_inline_result") ChosenInlineResult chosenInlineResult
) {

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
        this(updateId, message, editedMessage, channelPost, editedChannelPost, callbackQuery, null, null, inlineQuery, chosenInlineResult);
    }
}
