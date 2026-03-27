package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.tardyon.botframework.telegram.api.model.Video;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaidMediaVideo(
    String type,
    Video video
) implements PaidMedia {
}

