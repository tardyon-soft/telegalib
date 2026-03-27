package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendPhotoRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("business_connection_id") String businessConnectionId,
    InputFile photo,
    String caption,
    @JsonProperty("reply_markup") ReplyMarkup replyMarkup
) {
    public SendPhotoRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(photo, "photo must not be null");
    }

    public static SendPhotoRequest of(long chatId, InputFile photo) {
        return new SendPhotoRequest(chatId, null, photo, null, null);
    }

    public static SendPhotoRequest of(String chatId, InputFile photo) {
        return new SendPhotoRequest(chatId, null, photo, null, null);
    }

    public SendPhotoRequest(
        Object chatId,
        InputFile photo,
        String caption,
        ReplyMarkup replyMarkup
    ) {
        this(chatId, null, photo, caption, replyMarkup);
    }
}
