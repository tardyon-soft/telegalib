package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StarTransaction(
    String id,
    StarAmount amount,
    Integer date,
    TransactionPartner source,
    TransactionPartner receiver
) {
}

