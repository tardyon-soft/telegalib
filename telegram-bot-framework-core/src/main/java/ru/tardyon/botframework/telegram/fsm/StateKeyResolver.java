package ru.tardyon.botframework.telegram.fsm;

import java.util.Optional;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.InaccessibleMessage;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;

public final class StateKeyResolver {

    private StateKeyResolver() {
    }

    public static Optional<StateKey> resolve(Update update, String botId) {
        if (update == null || botId == null || botId.isBlank()) {
            return Optional.empty();
        }

        Message message = firstMessage(update);
        if (message != null) {
            return fromMessage(message, botId);
        }

        CallbackQuery callbackQuery = update.callbackQuery();
        if (callbackQuery != null) {
            return fromCallbackQuery(callbackQuery, botId);
        }

        return Optional.empty();
    }

    private static Message firstMessage(Update update) {
        if (update.message() != null) {
            return update.message();
        }
        if (update.editedMessage() != null) {
            return update.editedMessage();
        }
        if (update.channelPost() != null) {
            return update.channelPost();
        }
        if (update.editedChannelPost() != null) {
            return update.editedChannelPost();
        }
        if (update.businessMessage() != null) {
            return update.businessMessage();
        }
        if (update.editedBusinessMessage() != null) {
            return update.editedBusinessMessage();
        }
        return null;
    }

    private static Optional<StateKey> fromMessage(Message message, String botId) {
        if (message.chat() == null || message.from() == null) {
            return Optional.empty();
        }
        return Optional.of(new StateKey(botId, message.chat().id(), message.from().id()));
    }

    private static Optional<StateKey> fromCallbackQuery(CallbackQuery callbackQuery, String botId) {
        User from = callbackQuery.from();
        if (from == null || callbackQuery.message() == null) {
            return Optional.empty();
        }

        Chat chat = null;
        if (callbackQuery.message() instanceof Message message) {
            chat = message.chat();
        } else if (callbackQuery.message() instanceof InaccessibleMessage inaccessibleMessage) {
            chat = inaccessibleMessage.chat();
        }
        if (chat == null) {
            return Optional.empty();
        }
        return Optional.of(new StateKey(botId, chat.id(), from.id()));
    }
}
