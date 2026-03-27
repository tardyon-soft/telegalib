package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record EditMessageTextRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
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
        return new EditMessageTextRequest(null, chatId, messageId, null, text);
    }

    public static EditMessageTextRequest forChatMessage(String chatId, int messageId, String text) {
        return new EditMessageTextRequest(null, chatId, messageId, null, text);
    }

    public static EditMessageTextRequest forInlineMessage(String inlineMessageId, String text) {
        return new EditMessageTextRequest(null, null, null, Objects.requireNonNull(inlineMessageId), text);
    }

    public static EditMessageTextRequest forBusinessChatMessage(
        String businessConnectionId,
        long chatId,
        int messageId,
        String text
    ) {
        return new EditMessageTextRequest(
            Objects.requireNonNull(businessConnectionId, "businessConnectionId must not be null"),
            chatId,
            messageId,
            null,
            text
        );
    }

    public EditMessageTextRequest(Object chatId, Integer messageId, String inlineMessageId, String text) {
        this(null, chatId, messageId, inlineMessageId, text);
    }
}
