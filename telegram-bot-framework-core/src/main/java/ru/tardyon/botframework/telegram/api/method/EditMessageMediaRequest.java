package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.media.InputMedia;

public record EditMessageMediaRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_id") Integer messageId,
    @JsonProperty("inline_message_id") String inlineMessageId,
    InputMedia media,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup
) {
    public EditMessageMediaRequest {
        Objects.requireNonNull(media, "media must not be null");
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

    public static EditMessageMediaRequest forChatMessage(long chatId, int messageId, InputMedia media) {
        return new EditMessageMediaRequest(null, chatId, messageId, null, media, null);
    }

    public static EditMessageMediaRequest forChatMessage(String chatId, int messageId, InputMedia media) {
        return new EditMessageMediaRequest(null, chatId, messageId, null, media, null);
    }

    public static EditMessageMediaRequest forInlineMessage(String inlineMessageId, InputMedia media) {
        return new EditMessageMediaRequest(null, null, null, Objects.requireNonNull(inlineMessageId), media, null);
    }
}
