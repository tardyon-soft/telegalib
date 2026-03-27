package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import ru.tardyon.botframework.telegram.api.model.checklist.InputChecklist;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;

public record SendChecklistRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Long chatId,
    InputChecklist checklist,
    @JsonProperty("disable_notification") Boolean disableNotification,
    @JsonProperty("protect_content") Boolean protectContent,
    @JsonProperty("message_effect_id") String messageEffectId,
    @JsonProperty("reply_parameters") JsonNode replyParameters,
    @JsonProperty("reply_markup") InlineKeyboardMarkup replyMarkup
) {

    public SendChecklistRequest {
        if (businessConnectionId == null || businessConnectionId.isBlank()) {
            throw new IllegalArgumentException("businessConnectionId must not be blank");
        }
        if (chatId == null) {
            throw new IllegalArgumentException("chatId must not be null");
        }
        if (checklist == null) {
            throw new IllegalArgumentException("checklist must not be null");
        }
    }
}
