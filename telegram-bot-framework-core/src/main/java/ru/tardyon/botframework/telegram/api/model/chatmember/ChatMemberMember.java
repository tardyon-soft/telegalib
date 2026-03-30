package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

public record ChatMemberMember(
    String status,
    User user,
    @JsonProperty("tag") String tag
) implements ChatMember {
}
