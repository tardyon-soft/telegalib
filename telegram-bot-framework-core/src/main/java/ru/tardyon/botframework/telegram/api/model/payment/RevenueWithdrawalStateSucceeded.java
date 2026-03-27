package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RevenueWithdrawalStateSucceeded(
    String type,
    Integer date,
    String url
) implements RevenueWithdrawalState {
}

