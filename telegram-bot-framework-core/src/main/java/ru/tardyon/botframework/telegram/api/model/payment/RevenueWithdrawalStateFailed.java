package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RevenueWithdrawalStateFailed(String type) implements RevenueWithdrawalState {
}

