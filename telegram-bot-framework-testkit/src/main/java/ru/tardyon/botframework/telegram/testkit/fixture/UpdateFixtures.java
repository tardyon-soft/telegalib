package ru.tardyon.botframework.telegram.testkit.fixture;

import java.util.List;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;

/**
 * Builder-style helpers for quick Update fixture creation.
 */
public final class UpdateFixtures {

    private UpdateFixtures() {
    }

    public static Update messageUpdate(long updateId, long chatId, long userId, String text) {
        Chat chat = new Chat(chatId, "private", null, null, null, null, null);
        User user = new User(userId, false, "User" + userId, null, null, null, null, null, null);
        Message message = new Message(1, user, chat, (int) (System.currentTimeMillis() / 1000L), text, List.of(), null, null);
        return new Update(updateId, message, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static Update callbackUpdate(long updateId, String callbackQueryId, long chatId, long userId, String data) {
        Chat chat = new Chat(chatId, "private", null, null, null, null, null);
        User user = new User(userId, false, "User" + userId, null, null, null, null, null, null);
        Message callbackMessage = new Message(7, user, chat, (int) (System.currentTimeMillis() / 1000L), "callback-source", List.of(), null, null);
        CallbackQuery callbackQuery = new CallbackQuery(callbackQueryId, user, callbackMessage, null, "chat-instance", data, null);
        return new Update(updateId, null, null, null, null, callbackQuery, null, null, null, null, null, null, null, null);
    }
}
