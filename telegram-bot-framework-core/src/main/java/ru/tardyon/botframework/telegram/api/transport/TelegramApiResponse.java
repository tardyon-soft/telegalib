package ru.tardyon.botframework.telegram.api.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramApiResponse<T>(
    Boolean ok,
    T result,
    String description,
    @JsonProperty("error_code") Integer errorCode,
    ResponseParameters parameters
) {
}
