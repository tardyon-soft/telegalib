package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendDocumentRequest(
    @JsonProperty("chat_id") Object chatId,
    InputFile document,
    String caption,
    @JsonProperty("reply_markup") ReplyMarkup replyMarkup
) {
    public SendDocumentRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(document, "document must not be null");
    }

    public static SendDocumentRequest of(long chatId, InputFile document) {
        return new SendDocumentRequest(chatId, document, null, null);
    }

    public static SendDocumentRequest of(String chatId, InputFile document) {
        return new SendDocumentRequest(chatId, document, null, null);
    }
}
