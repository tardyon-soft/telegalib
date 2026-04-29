package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public record CopyMessageRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("from_chat_id") Object fromChatId,
    @JsonProperty("message_id") Integer messageId,
    String caption,
    @JsonProperty("reply_markup") ReplyMarkup replyMarkup
) {
    public CopyMessageRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(fromChatId, "fromChatId must not be null");
        Objects.requireNonNull(messageId, "messageId must not be null");
    }
}
