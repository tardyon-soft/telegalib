package ru.tardyon.botframework.telegram.api.model.story;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.tardyon.botframework.telegram.api.model.Chat;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Story(
    Chat chat,
    Integer id
) {
}
