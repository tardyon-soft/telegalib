package ru.tardyon.botframework.telegram.dispatcher.handler;

import ru.tardyon.botframework.telegram.api.model.business.BusinessMessagesDeleted;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

@FunctionalInterface
public interface BusinessMessagesDeletedHandler {

    void handle(UpdateContext context, BusinessMessagesDeleted deleted);
}
