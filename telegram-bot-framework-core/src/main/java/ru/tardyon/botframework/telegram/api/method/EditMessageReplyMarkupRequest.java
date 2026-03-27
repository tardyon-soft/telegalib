package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public record EditMessageReplyMarkupRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_id") Integer messageId,
    @JsonProperty("inline_message_id") String inlineMessageId,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup
) {
    public EditMessageReplyMarkupRequest {
        boolean hasChatMessageTarget = chatId != null || messageId != null;
        boolean hasCompleteChatMessageTarget = chatId != null && messageId != null;
        boolean hasInlineTarget = inlineMessageId != null;

        if (hasChatMessageTarget && !hasCompleteChatMessageTarget) {
            throw new IllegalArgumentException("Both chatId and messageId must be provided together");
        }
        if (hasCompleteChatMessageTarget == hasInlineTarget) {
            throw new IllegalArgumentException("Either chatId+messageId or inlineMessageId must be provided");
        }
    }

    public static EditMessageReplyMarkupRequest forChatMessage(long chatId, int messageId, InlineKeyboardMarkup replyMarkup) {
        return new EditMessageReplyMarkupRequest(chatId, messageId, null, replyMarkup);
    }

    public static EditMessageReplyMarkupRequest forChatMessage(String chatId, int messageId, InlineKeyboardMarkup replyMarkup) {
        return new EditMessageReplyMarkupRequest(chatId, messageId, null, replyMarkup);
    }

    public static EditMessageReplyMarkupRequest forInlineMessage(String inlineMessageId, InlineKeyboardMarkup replyMarkup) {
        return new EditMessageReplyMarkupRequest(null, null, inlineMessageId, replyMarkup);
    }
}
