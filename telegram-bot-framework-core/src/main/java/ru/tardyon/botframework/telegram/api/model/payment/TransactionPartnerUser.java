package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.tardyon.botframework.telegram.api.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionPartnerUser(
    String type,
    User user
) implements TransactionPartner {
}

