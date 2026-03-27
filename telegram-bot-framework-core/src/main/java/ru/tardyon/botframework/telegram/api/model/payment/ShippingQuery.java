package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShippingQuery(
    String id,
    User from,
    @JsonProperty("invoice_payload") String invoicePayload,
    @JsonProperty("shipping_address") ShippingAddress shippingAddress
) {
}
