package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.model.ReplyParameters;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendDocumentRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("business_connection_id") String businessConnectionId,
    InputFile document,
    String caption,
    @JsonProperty("reply_markup") ReplyMarkup replyMarkup,
    @JsonProperty("reply_parameters") ReplyParameters replyParameters
) {
    public SendDocumentRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(document, "document must not be null");
    }

    public static SendDocumentRequest of(long chatId, InputFile document) {
        return new SendDocumentRequest(chatId, null, document, null, null);
    }

    public static SendDocumentRequest of(String chatId, InputFile document) {
        return new SendDocumentRequest(chatId, null, document, null, null);
    }

    public SendDocumentRequest(
        Object chatId,
        String businessConnectionId,
        InputFile document,
        String caption,
        ReplyMarkup replyMarkup
    ) {
        this(chatId, businessConnectionId, document, caption, replyMarkup, null);
    }

    public SendDocumentRequest(
        Object chatId,
        InputFile document,
        String caption,
        ReplyMarkup replyMarkup
    ) {
        this(chatId, null, document, caption, replyMarkup, null);
    }

    public SendDocumentRequest withReplyTo(int messageId) {
        return new SendDocumentRequest(chatId, businessConnectionId, document, caption, replyMarkup, ReplyParameters.of(messageId));
    }
}
