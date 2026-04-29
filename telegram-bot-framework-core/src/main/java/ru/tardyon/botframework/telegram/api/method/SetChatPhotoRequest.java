package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;

public record SetChatPhotoRequest(
    @JsonProperty("chat_id") Object chatId,
    InputFile photo
) {
    public SetChatPhotoRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(photo, "photo must not be null");
    }
}
