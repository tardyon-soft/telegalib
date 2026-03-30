package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tardyon.botframework.telegram.api.model.User;

public record ChatMemberRestricted(
    String status,
    User user,
    @JsonProperty("is_member") Boolean isMember,
    @JsonProperty("until_date") Long untilDate,
    @JsonProperty("tag") String tag
) implements ChatMember {
}
