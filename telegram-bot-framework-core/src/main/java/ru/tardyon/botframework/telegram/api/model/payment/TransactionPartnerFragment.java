package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionPartnerFragment(
    String type,
    @JsonProperty("withdrawal_state") RevenueWithdrawalState withdrawalState
) implements TransactionPartner {
}

