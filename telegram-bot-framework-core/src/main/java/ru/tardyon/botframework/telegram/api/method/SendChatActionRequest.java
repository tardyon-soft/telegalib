package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record SendChatActionRequest(
    @JsonProperty("business_connection_id") String businessConnectionId,
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("message_thread_id") Integer messageThreadId,
    String action
) {
    public SendChatActionRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(action, "action must not be null");
        if (action.isBlank()) {
            throw new IllegalArgumentException("action must not be blank");
        }
    }

    public static SendChatActionRequest typing(long chatId) {
        return new SendChatActionRequest(null, chatId, null, "typing");
    }

    public static SendChatActionRequest uploadPhoto(long chatId) {
        return new SendChatActionRequest(null, chatId, null, "upload_photo");
    }
}
