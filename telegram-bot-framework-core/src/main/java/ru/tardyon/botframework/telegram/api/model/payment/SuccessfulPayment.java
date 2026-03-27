package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SuccessfulPayment(
    String currency,
    @JsonProperty("total_amount") Integer totalAmount,
    @JsonProperty("invoice_payload") String invoicePayload,
    @JsonProperty("shipping_option_id") String shippingOptionId,
    @JsonProperty("order_info") OrderInfo orderInfo,
    @JsonProperty("telegram_payment_charge_id") String telegramPaymentChargeId,
    @JsonProperty("provider_payment_charge_id") String providerPaymentChargeId
) {
}
