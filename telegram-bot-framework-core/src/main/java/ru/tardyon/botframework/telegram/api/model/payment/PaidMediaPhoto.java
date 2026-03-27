package ru.tardyon.botframework.telegram.api.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.PhotoSize;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaidMediaPhoto(
    String type,
    List<PhotoSize> photo
) implements PaidMedia {
}

