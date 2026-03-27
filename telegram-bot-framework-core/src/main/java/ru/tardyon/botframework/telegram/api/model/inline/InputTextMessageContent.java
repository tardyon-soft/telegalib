package ru.tardyon.botframework.telegram.api.model.inline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;

public record InputTextMessageContent(
    @JsonProperty("message_text") String messageText,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("entities") List<MessageEntity> entities
) implements InputMessageContent {

    public InputTextMessageContent {
        Objects.requireNonNull(messageText, "messageText must not be null");
        if (messageText.isBlank()) {
            throw new IllegalArgumentException("messageText must not be blank");
        }
    }

    public static InputTextMessageContent of(String messageText) {
        return new InputTextMessageContent(messageText, null, null);
    }
}
