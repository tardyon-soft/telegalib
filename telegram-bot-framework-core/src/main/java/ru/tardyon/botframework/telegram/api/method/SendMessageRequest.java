package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record SendMessageRequest(
    @JsonProperty("chat_id") Object chatId,
    String text,
    @JsonProperty("reply_markup") ReplyMarkup replyMarkup
) {
    public SendMessageRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(text, "text must not be null");
    }

    public static SendMessageRequest of(long chatId, String text) {
        return new SendMessageRequest(chatId, text, null);
    }

    public static SendMessageRequest of(String chatId, String text) {
        return new SendMessageRequest(chatId, text, null);
    }

    public static SendMessageRequest of(long chatId, String text, ReplyMarkup replyMarkup) {
        return new SendMessageRequest(chatId, text, replyMarkup);
    }

    public static SendMessageRequest of(String chatId, String text, ReplyMarkup replyMarkup) {
        return new SendMessageRequest(chatId, text, replyMarkup);
    }
}
