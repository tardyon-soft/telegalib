package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EditChatSubscriptionInviteLinkRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("invite_link") String inviteLink,
    String name
) {

    public EditChatSubscriptionInviteLinkRequest {
        if (chatId == null) {
            throw new IllegalArgumentException("chatId must not be null");
        }
        if (inviteLink == null || inviteLink.isBlank()) {
            throw new IllegalArgumentException("inviteLink must not be blank");
        }
        if (name != null && name.length() > 32) {
            throw new IllegalArgumentException("name length must be in range 0..32");
        }
    }
}
