package ru.tardyon.botframework.telegram.api.model.webapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PreparedInlineMessage(
    String id,
    @JsonProperty("expiration_date") Long expirationDate
) {
}
