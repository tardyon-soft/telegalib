package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.tardyon.botframework.telegram.api.model.Chat;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionPartnerChat(
    String type,
    Chat chat
) implements TransactionPartner {
}

