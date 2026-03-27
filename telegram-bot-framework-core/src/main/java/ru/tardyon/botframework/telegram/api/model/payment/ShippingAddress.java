package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShippingAddress(
    @JsonProperty("country_code") String countryCode,
    String state,
    String city,
    @JsonProperty("street_line1") String streetLine1,
    @JsonProperty("street_line2") String streetLine2,
    @JsonProperty("post_code") String postCode
) {
}
