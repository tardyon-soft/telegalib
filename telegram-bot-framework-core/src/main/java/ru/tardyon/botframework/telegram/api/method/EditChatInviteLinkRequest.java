package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record EditChatInviteLinkRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("invite_link") String inviteLink,
    String name,
    @JsonProperty("expire_date") Integer expireDate,
    @JsonProperty("member_limit") Integer memberLimit,
    @JsonProperty("creates_join_request") Boolean createsJoinRequest
) {
    public EditChatInviteLinkRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(inviteLink, "inviteLink must not be null");
        if (inviteLink.isBlank()) {
            throw new IllegalArgumentException("inviteLink must not be blank");
        }
        if (name != null && name.length() > 32) {
            throw new IllegalArgumentException("name length must be in range 0..32");
        }
        if (memberLimit != null && (memberLimit < 1 || memberLimit > 99_999)) {
            throw new IllegalArgumentException("memberLimit must be in range 1..99999");
        }
        if (Boolean.TRUE.equals(createsJoinRequest) && memberLimit != null) {
            throw new IllegalArgumentException("memberLimit can't be specified when createsJoinRequest is true");
        }
    }
}
