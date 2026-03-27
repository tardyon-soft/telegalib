package ru.tardyon.botframework.telegram.api.model.webapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebAppData(
    String data,
    @JsonProperty("button_text") String buttonText
) {
}
