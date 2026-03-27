package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record EditUserStarSubscriptionRequest(
    @JsonProperty("user_id") Long userId,
    @JsonProperty("telegram_payment_charge_id") String telegramPaymentChargeId,
    @JsonProperty("is_canceled") Boolean isCanceled
) {

    public EditUserStarSubscriptionRequest {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(telegramPaymentChargeId, "telegramPaymentChargeId must not be null");
        Objects.requireNonNull(isCanceled, "isCanceled must not be null");
        if (telegramPaymentChargeId.isBlank()) {
            throw new IllegalArgumentException("telegramPaymentChargeId must not be blank");
        }
    }
}

