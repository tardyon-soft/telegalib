package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.ReplyParameters;
import ru.tardyon.botframework.telegram.api.model.payment.LabeledPrice;

public record SendInvoiceRequest(
    @JsonProperty("chat_id") Object chatId,
    String title,
    String description,
    String payload,
    @JsonProperty("provider_token") String providerToken,
    String currency,
    List<LabeledPrice> prices,
    @JsonProperty("max_tip_amount") Integer maxTipAmount,
    @JsonProperty("suggested_tip_amounts") List<Integer> suggestedTipAmounts,
    @JsonProperty("start_parameter") String startParameter,
    @JsonProperty("provider_data") String providerData,
    @JsonProperty("photo_url") String photoUrl,
    @JsonProperty("photo_size") Integer photoSize,
    @JsonProperty("photo_width") Integer photoWidth,
    @JsonProperty("photo_height") Integer photoHeight,
    @JsonProperty("need_name") Boolean needName,
    @JsonProperty("need_phone_number") Boolean needPhoneNumber,
    @JsonProperty("need_email") Boolean needEmail,
    @JsonProperty("need_shipping_address") Boolean needShippingAddress,
    @JsonProperty("is_flexible") Boolean isFlexible,
    @JsonProperty("reply_parameters") ReplyParameters replyParameters
) {

    public SendInvoiceRequest(
        Object chatId,
        String title,
        String description,
        String payload,
        String providerToken,
        String currency,
        List<LabeledPrice> prices,
        Integer maxTipAmount,
        List<Integer> suggestedTipAmounts,
        String startParameter,
        String providerData,
        String photoUrl,
        Integer photoSize,
        Integer photoWidth,
        Integer photoHeight,
        Boolean needName,
        Boolean needPhoneNumber,
        Boolean needEmail,
        Boolean needShippingAddress,
        Boolean isFlexible
    ) {
        this(
            chatId,
            title,
            description,
            payload,
            providerToken,
            currency,
            prices,
            maxTipAmount,
            suggestedTipAmounts,
            startParameter,
            providerData,
            photoUrl,
            photoSize,
            photoWidth,
            photoHeight,
            needName,
            needPhoneNumber,
            needEmail,
            needShippingAddress,
            isFlexible,
            null
        );
    }

    public SendInvoiceRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        Objects.requireNonNull(prices, "prices must not be null");
        if (prices.isEmpty()) {
            throw new IllegalArgumentException("prices must not be empty");
        }
        prices = List.copyOf(prices);
        if (suggestedTipAmounts != null) {
            suggestedTipAmounts = List.copyOf(suggestedTipAmounts);
        }
    }

    public SendInvoiceRequest withReplyTo(int messageId) {
        return new SendInvoiceRequest(
            chatId,
            title,
            description,
            payload,
            providerToken,
            currency,
            prices,
            maxTipAmount,
            suggestedTipAmounts,
            startParameter,
            providerData,
            photoUrl,
            photoSize,
            photoWidth,
            photoHeight,
            needName,
            needPhoneNumber,
            needEmail,
            needShippingAddress,
            isFlexible,
            ReplyParameters.of(messageId)
        );
    }
}
