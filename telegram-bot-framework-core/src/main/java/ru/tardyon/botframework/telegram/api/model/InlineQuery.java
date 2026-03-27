package ru.tardyon.botframework.telegram.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InlineQuery(
    String id,
    User from,
    Location location,
    String query,
    String offset,
    @JsonProperty("chat_type") String chatType
) {
}
