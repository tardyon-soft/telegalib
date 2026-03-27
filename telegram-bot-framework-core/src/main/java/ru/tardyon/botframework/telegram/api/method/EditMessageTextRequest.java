package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record EditMessageTextRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_id") Integer messageId,
    @JsonProperty("inline_message_id") String inlineMessageId,
    String text
) {
    public EditMessageTextRequest {
        Objects.requireNonNull(text, "text must not be null");
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

    public static EditMessageTextRequest forChatMessage(long chatId, int messageId, String text) {
        return new EditMessageTextRequest(chatId, messageId, null, text);
    }

    public static EditMessageTextRequest forChatMessage(String chatId, int messageId, String text) {
        return new EditMessageTextRequest(chatId, messageId, null, text);
    }

    public static EditMessageTextRequest forInlineMessage(String inlineMessageId, String text) {
        return new EditMessageTextRequest(null, null, Objects.requireNonNull(inlineMessageId), text);
    }
}
