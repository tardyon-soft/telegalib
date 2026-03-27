package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingOption;

public record AnswerShippingQueryRequest(
    @JsonProperty("shipping_query_id") String shippingQueryId,
    Boolean ok,
    @JsonProperty("shipping_options") List<ShippingOption> shippingOptions,
    @JsonProperty("error_message") String errorMessage
) {

    public AnswerShippingQueryRequest {
        Objects.requireNonNull(shippingQueryId, "shippingQueryId must not be null");
        Objects.requireNonNull(ok, "ok must not be null");
        if (Boolean.TRUE.equals(ok)) {
            if (shippingOptions == null || shippingOptions.isEmpty()) {
                throw new IllegalArgumentException("shippingOptions must not be empty when ok=true");
            }
            if (errorMessage != null && !errorMessage.isBlank()) {
                throw new IllegalArgumentException("errorMessage must be null when ok=true");
            }
            shippingOptions = List.copyOf(shippingOptions);
        } else {
            if (errorMessage == null || errorMessage.isBlank()) {
                throw new IllegalArgumentException("errorMessage must not be blank when ok=false");
            }
            if (shippingOptions != null && !shippingOptions.isEmpty()) {
                throw new IllegalArgumentException("shippingOptions must be null or empty when ok=false");
            }
            shippingOptions = shippingOptions == null ? null : List.copyOf(shippingOptions);
        }
    }
}
