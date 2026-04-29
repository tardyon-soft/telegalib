package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public record EditMessageCaptionRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_id") Integer messageId,
    @JsonProperty("inline_message_id") String inlineMessageId,
    String caption,
    @JsonProperty("parse_mode") String parseMode,
    @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
    @JsonProperty("show_caption_above_media") Boolean showCaptionAboveMedia,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup
) {
    public EditMessageCaptionRequest {
        boolean hasChatMessageTarget = chatId != null || messageId != null;
        boolean hasCompleteChatMessageTarget = chatId != null && messageId != null;
        boolean hasInlineTarget = inlineMessageId != null;

        if (hasChatMessageTarget && !hasCompleteChatMessageTarget) {
            throw new IllegalArgumentException("Both chatId and messageId must be provided together");
        }
        if (hasCompleteChatMessageTarget == hasInlineTarget) {
            throw new IllegalArgumentException("Either chatId+messageId or inlineMessageId must be provided");
        }
        captionEntities = captionEntities == null ? null : List.copyOf(captionEntities);
    }

    public static EditMessageCaptionRequest forChatMessage(long chatId, int messageId, String caption) {
        return new EditMessageCaptionRequest(null, chatId, messageId, null, caption, null, null, null, null);
    }

    public static EditMessageCaptionRequest forChatMessage(String chatId, int messageId, String caption) {
        return new EditMessageCaptionRequest(null, chatId, messageId, null, caption, null, null, null, null);
    }

    public static EditMessageCaptionRequest forInlineMessage(String inlineMessageId, String caption) {
        return new EditMessageCaptionRequest(null, null, null, Objects.requireNonNull(inlineMessageId), caption, null, null, null, null);
    }
}
