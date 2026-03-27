package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.databind.JsonNode;

public record TransactionPartnerUnknown(
    String type,
    JsonNode raw
) implements TransactionPartner {
}

