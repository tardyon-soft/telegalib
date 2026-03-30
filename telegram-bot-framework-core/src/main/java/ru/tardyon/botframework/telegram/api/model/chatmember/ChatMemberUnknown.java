package ru.tardyon.botframework.telegram.api.model.chatmember;

import com.fasterxml.jackson.databind.JsonNode;
import ru.tardyon.botframework.telegram.api.model.User;

public record ChatMemberUnknown(
    String status,
    User user,
    JsonNode raw
) implements ChatMember {
}
