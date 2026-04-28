package ru.tardyon.botframework.telegram.dispatcher.handler;

import ru.tardyon.botframework.telegram.api.model.chatmember.ChatMemberUpdated;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface ChatMemberUpdatedHandler {

    void handle(UpdateContext context, ChatMemberUpdated chatMemberUpdated);
}
