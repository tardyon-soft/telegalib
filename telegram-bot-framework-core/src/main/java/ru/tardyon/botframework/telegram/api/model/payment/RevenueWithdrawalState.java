package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = RevenueWithdrawalStatePending.class, name = "pending"),
        @JsonSubTypes.Type(value = RevenueWithdrawalStateSucceeded.class, name = "succeeded"),
        @JsonSubTypes.Type(value = RevenueWithdrawalStateFailed.class, name = "failed")
    }
)
public sealed interface RevenueWithdrawalState
    permits RevenueWithdrawalStatePending, RevenueWithdrawalStateSucceeded, RevenueWithdrawalStateFailed {

    String type();
}

