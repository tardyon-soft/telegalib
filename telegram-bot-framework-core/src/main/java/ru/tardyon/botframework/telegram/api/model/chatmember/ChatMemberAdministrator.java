package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

public record ChatMemberAdministrator(
    String status,
    User user,
    @JsonProperty("can_be_edited") Boolean canBeEdited,
    @JsonProperty("is_anonymous") Boolean isAnonymous,
    @JsonProperty("custom_title") String customTitle
) implements ChatMember {
}
