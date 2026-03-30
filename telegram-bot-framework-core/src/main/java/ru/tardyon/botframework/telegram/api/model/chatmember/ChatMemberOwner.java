package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

public record ChatMemberOwner(
    String status,
    User user,
    @JsonProperty("is_anonymous") boolean isAnonymous,
    @JsonProperty("custom_title") String customTitle
) implements ChatMember {
}
