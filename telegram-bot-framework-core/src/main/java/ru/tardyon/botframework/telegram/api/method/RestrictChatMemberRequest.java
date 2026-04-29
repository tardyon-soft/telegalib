package ru.tardyon.botframework.telegram.api.method;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.ChatPermissions;

public record RestrictChatMemberRequest(
    @JsonProperty("chat_id") Object chatId,
    @JsonProperty("user_id") long userId,
    ChatPermissions permissions,
    @JsonProperty("use_independent_chat_permissions") Boolean useIndependentChatPermissions,
    @JsonProperty("until_date") Long untilDate
) {
    public RestrictChatMemberRequest {
        Objects.requireNonNull(chatId, "chatId must not be null");
        Objects.requireNonNull(permissions, "permissions must not be null");
    }
}
