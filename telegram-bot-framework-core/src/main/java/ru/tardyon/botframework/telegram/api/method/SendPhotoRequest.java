package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.ReplyParameters;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendPhotoRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("business_connection_id") String businessConnectionId,
    InputFile photo,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("reply_markup") ReplyMarkup replyMarkup,
    @JsonProperty("reply_parameters") ReplyParameters replyParameters
) {
    public SendPhotoRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(photo, "photo must not be null");
    }

    public SendPhotoRequest(
        Object chatId,
        String businessConnectionId,
        InputFile photo,
        String caption,
        String parseMode,
        ReplyMarkup replyMarkup
    ) {
        this(chatId, businessConnectionId, photo, caption, parseMode, replyMarkup, null);
    }

    public SendPhotoRequest(
        Object chatId,
        String businessConnectionId,
        InputFile photo,
        String caption,
        ReplyMarkup replyMarkup
    ) {
        this(chatId, businessConnectionId, photo, caption, null, replyMarkup, null);
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

    public SendPhotoRequest(
        Object chatId,
        InputFile photo,
        String caption,
        String parseMode,
        ReplyMarkup replyMarkup
    ) {
        this(chatId, null, photo, caption, parseMode, replyMarkup, null);
    }

    public SendPhotoRequest withReplyTo(int messageId) {
        return new SendPhotoRequest(chatId, businessConnectionId, photo, caption, parseMode, replyMarkup, ReplyParameters.of(messageId));
    }
}
