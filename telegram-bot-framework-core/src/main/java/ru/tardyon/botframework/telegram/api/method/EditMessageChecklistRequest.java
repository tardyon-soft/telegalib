package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.checklist.InputChecklist;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public record EditMessageChecklistRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Long chatId,
    @JsonProperty("message_id") Integer messageId,
    InputChecklist checklist,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup
) {

    public EditMessageChecklistRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (chatId == null) {
            throw new IllegalArgumentException("chatId must not be null");
        }
        if (messageId == null) {
            throw new IllegalArgumentException("messageId must not be null");
        }
        if (checklist == null) {
            throw new IllegalArgumentException("checklist must not be null");
        }
    }
}
