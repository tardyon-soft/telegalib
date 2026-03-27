package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaidMediaPurchased(
    User from,
    @JsonProperty("paid_media_payload") String paidMediaPayload
) {
}

