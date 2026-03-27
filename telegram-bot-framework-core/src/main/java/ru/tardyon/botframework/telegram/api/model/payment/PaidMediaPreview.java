package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaidMediaPreview(
    String type,
    Integer width,
    Integer height,
    Integer duration
) implements PaidMedia {
}

