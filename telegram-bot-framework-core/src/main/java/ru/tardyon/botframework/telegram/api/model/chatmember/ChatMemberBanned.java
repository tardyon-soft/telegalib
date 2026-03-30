package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

public record ChatMemberBanned(
    String status,
    User user,
    @JsonProperty("until_date") Long untilDate
) implements ChatMember {
}
