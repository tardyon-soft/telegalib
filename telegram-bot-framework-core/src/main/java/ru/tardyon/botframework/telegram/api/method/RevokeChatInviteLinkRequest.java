package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record RevokeChatInviteLinkRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("invite_link") String inviteLink
) {
    public RevokeChatInviteLinkRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(inviteLink, "inviteLink must not be null");
        if (inviteLink.isBlank()) {
            throw new IllegalArgumentException("inviteLink must not be blank");
        }
    }
}
