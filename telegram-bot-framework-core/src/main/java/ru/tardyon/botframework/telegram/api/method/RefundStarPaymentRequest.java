package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record RefundStarPaymentRequest(
    @JsonProperty("user_id") Long userId,
    @JsonProperty("telegram_payment_charge_id") String telegramPaymentChargeId
) {

    public RefundStarPaymentRequest {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(telegramPaymentChargeId, "telegramPaymentChargeId must not be null");
        if (telegramPaymentChargeId.isBlank()) {
            throw new IllegalArgumentException("telegramPaymentChargeId must not be blank");
        }
    }
}

