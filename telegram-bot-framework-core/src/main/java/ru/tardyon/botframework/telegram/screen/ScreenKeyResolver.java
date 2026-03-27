package ru.tardyon.botframework.telegram.screen;

import java.util.Optional;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.InaccessibleMessage;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;

public final class ScreenKeyResolver {

    private ScreenKeyResolver() {
    }

    public static Optional<ScreenKey> resolve(Update update, String botId) {
        if (update == null || botId == null || botId.isBlank()) {
            return Optional.empty();
        }

        Message message = firstMessage(update);
        if (message != null && message.chat() != null) {
            return Optional.of(new ScreenKey(botId, message.chat().id()));
        }

        CallbackQuery callbackQuery = update.callbackQuery();
        if (callbackQuery != null && callbackQuery.message() != null) {
            Chat chat = null;
            if (callbackQuery.message() instanceof Message callbackMessage) {
                chat = callbackMessage.chat();
            } else if (callbackQuery.message() instanceof InaccessibleMessage inaccessibleMessage) {
                chat = inaccessibleMessage.chat();
            }
            if (chat != null) {
                return Optional.of(new ScreenKey(botId, chat.id()));
            }
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
}
