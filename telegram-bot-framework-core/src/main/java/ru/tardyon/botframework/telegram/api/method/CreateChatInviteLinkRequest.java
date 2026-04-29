package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record CreateChatInviteLinkRequest(
    @JsonProperty("chat_id") Object chatId,
    String name,
    @JsonProperty("expire_date") Integer expireDate,
    @JsonProperty("member_limit") Integer memberLimit,
    @JsonProperty("creates_join_request") Boolean createsJoinRequest
) {
    public CreateChatInviteLinkRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        validateName(name);
        validateMemberLimit(memberLimit);
        if (Boolean.TRUE.equals(createsJoinRequest) && memberLimit != null) {
            throw new IllegalArgumentException("memberLimit can't be specified when createsJoinRequest is true");
        }
    }

    private static void validateName(String name) {
        if (name != null && name.length() > 32) {
            throw new IllegalArgumentException("name length must be in range 0..32");
        }
    }

    private static void validateMemberLimit(Integer memberLimit) {
        if (memberLimit != null && (memberLimit < 1 || memberLimit > 99_999)) {
            throw new IllegalArgumentException("memberLimit must be in range 1..99999");
        }
    }
}
