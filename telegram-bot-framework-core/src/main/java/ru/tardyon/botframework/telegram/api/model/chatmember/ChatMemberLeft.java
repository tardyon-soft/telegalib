package ru.tardyon.botframework.telegram.api.model.chatmember;

import ru.tardyon.botframework.telegram.api.model.User;

public record ChatMemberLeft(
    String status,
    User user
) implements ChatMember {
}
