package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.payment.AcceptedGiftTypes;

public record SetBusinessAccountGiftSettingsRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("show_gift_button") Boolean showGiftButton,
    @JsonProperty("accepted_gift_types") AcceptedGiftTypes acceptedGiftTypes
) {

    public SetBusinessAccountGiftSettingsRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        Objects.requireNonNull(showGiftButton, "showGiftButton must not be null");
        Objects.requireNonNull(acceptedGiftTypes, "acceptedGiftTypes must not be null");
    }
}
