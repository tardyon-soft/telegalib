package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.ReplyParameters;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendMessageRequest(
    @JsonProperty("chat_id") Object chatId,
    String text,
    @JsonProperty("reply_markup") ReplyMarkup replyMarkup,
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("reply_parameters") ReplyParameters replyParameters
) {
    public SendMessageRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(text, "text must not be null");
    }

    public SendMessageRequest(Object chatId, String text, ReplyMarkup replyMarkup, String businessConnectionId) {
        this(chatId, text, replyMarkup, businessConnectionId, null, null);
    }

    public static SendMessageRequest of(long chatId, String text) {
        return new SendMessageRequest(chatId, text, null, null);
    }

    public static SendMessageRequest of(String chatId, String text) {
        return new SendMessageRequest(chatId, text, null, null);
    }

    public static SendMessageRequest of(long chatId, String text, ReplyMarkup replyMarkup) {
        return new SendMessageRequest(chatId, text, replyMarkup, null);
    }

    public static SendMessageRequest of(String chatId, String text, ReplyMarkup replyMarkup) {
        return new SendMessageRequest(chatId, text, replyMarkup, null);
    }

    public static SendMessageRequest ofBusiness(long chatId, String text, String businessConnectionId) {
        return new SendMessageRequest(chatId, text, null, Objects.requireNonNull(businessConnectionId, "businessConnectionId must not be null"));
    }

    public SendMessageRequest(Object chatId, String text, ReplyMarkup replyMarkup) {
        this(chatId, text, replyMarkup, null);
    }

    public SendMessageRequest(Object chatId, String text, ReplyMarkup replyMarkup, String businessConnectionId, String parseMode) {
        this(chatId, text, replyMarkup, businessConnectionId, parseMode, null);
    }

    public SendMessageRequest withReplyTo(int messageId) {
        return new SendMessageRequest(chatId, text, replyMarkup, businessConnectionId, parseMode, ReplyParameters.of(messageId));
    }
}
