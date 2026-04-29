package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record PinChatMessageRequest(@JsonProperty("business_connection_id") String businessConnectionId, @JsonProperty("chat_id") Object chatId, @JsonProperty("message_id") Integer messageId, @JsonProperty("disable_notification") Boolean disableNotification) {
    public PinChatMessageRequest { Objects.requireNonNull(chatId, "chatId must not be null"); Objects.requireNonNull(messageId, "messageId must not be null"); }
}
