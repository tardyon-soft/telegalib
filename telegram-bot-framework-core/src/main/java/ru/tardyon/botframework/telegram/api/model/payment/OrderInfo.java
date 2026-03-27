package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderInfo(
    String name,
    @JsonProperty("phone_number") String phoneNumber,
    String email,
    @JsonProperty("shipping_address") ShippingAddress shippingAddress
) {
}
