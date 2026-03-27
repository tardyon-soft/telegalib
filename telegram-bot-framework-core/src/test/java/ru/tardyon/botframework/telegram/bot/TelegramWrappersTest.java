package ru.tardyon.botframework.telegram.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.AnswerCallbackQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerInlineQueryRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMessageRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageTextRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.GetFileRequest;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.method.GetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SendMediaGroupRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.MaybeInaccessibleMessage;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.TelegramFile;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.WebhookInfo;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;

class TelegramWrappersTest {

    @Test
    void messageWrapperUsesSourceChatAndMessageIds() {
        FakeClient client = new FakeClient();
        Message source = new Message(
            77,
            new User(1L, false, "A", null, "a", "en", null, null, null),
            new Chat(555L, "private", null, null, null, null, null),
            1,
            "ping",
            null,
            null,
            null
        );

        TelegramMessage wrapper = new TelegramMessage(source, client);

        wrapper.reply("pong");
        wrapper.reply(new SendMessageRequest("wrong-chat", "pong2", null));
        wrapper.editText("edited");
        wrapper.editReplyMarkup(
            Keyboards.inlineKeyboard().row(Keyboards.callbackButton("Menu", "menu:1")).build()
        );
        wrapper.delete();

        assertEquals(555L, client.lastSendMessageRequest.chatId());
        assertEquals("pong2", client.lastSendMessageRequest.text());
        assertEquals(555L, client.lastEditMessageTextRequest.chatId());
        assertEquals(77, client.lastEditMessageTextRequest.messageId());
        assertEquals(555L, client.lastDeleteMessageRequest.chatId());
        assertEquals(77, client.lastDeleteMessageRequest.messageId());
        assertEquals("menu:1", client.lastEditMessageReplyMarkupRequest.replyMarkup().inlineKeyboard().get(0).get(0).callbackData());
    }

    @Test
    void callbackWrapperAnswersWithAndWithoutText() {
        FakeClient client = new FakeClient();
        CallbackQuery callbackQuery = new CallbackQuery("cb-id", null, null, null, "ci", "menu:1", null);

        TelegramCallbackQuery wrapper = new TelegramCallbackQuery(callbackQuery, client);
        assertTrue(wrapper.answer());
        assertTrue(wrapper.answer("OK"));

        assertEquals("cb-id", client.lastAnswerCallbackQueryRequest.callbackQueryId());
        assertEquals("OK", client.lastAnswerCallbackQueryRequest.text());
    }

    @Test
    void callbackWrapperExposesMessageWrapper() {
        FakeClient client = new FakeClient();
        Message message = new Message(
            77,
            null,
            new Chat(555L, "private", null, null, null, null, null),
            1,
            "ping",
            null,
            null,
            null
        );
        MaybeInaccessibleMessage callbackMessage = message;
        CallbackQuery callbackQuery = new CallbackQuery("cb-id", null, callbackMessage, null, "ci", "menu:1", null);

        TelegramCallbackQuery wrapper = new TelegramCallbackQuery(callbackQuery, client);
        TelegramMessage telegramMessage = wrapper.message();

        assertNotNull(telegramMessage);
        telegramMessage.editReplyMarkup(
            Keyboards.inlineKeyboard().row(Keyboards.callbackButton("Back", "menu:back")).build()
        );
        assertEquals("menu:back", client.lastEditMessageReplyMarkupRequest.replyMarkup().inlineKeyboard().get(0).get(0).callbackData());
    }

    private static final class FakeClient implements TelegramApiClient {

        private SendMessageRequest lastSendMessageRequest;
        private EditMessageTextRequest lastEditMessageTextRequest;
        private DeleteMessageRequest lastDeleteMessageRequest;
        private AnswerCallbackQueryRequest lastAnswerCallbackQueryRequest;
        private EditMessageReplyMarkupRequest lastEditMessageReplyMarkupRequest;

        @Override
        public User getMe() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Update> getUpdates(GetUpdatesRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message sendMessage(SendMessageRequest request) {
            this.lastSendMessageRequest = request;
            return null;
        }

        @Override
        public EditMessageTextResult editMessageText(EditMessageTextRequest request) {
            this.lastEditMessageTextRequest = request;
            return null;
        }

        @Override
        public EditMessageReplyMarkupResult editMessageReplyMarkup(EditMessageReplyMarkupRequest request) {
            this.lastEditMessageReplyMarkupRequest = request;
            return null;
        }

        @Override
        public boolean deleteMessage(DeleteMessageRequest request) {
            this.lastDeleteMessageRequest = request;
            return true;
        }

        @Override
        public boolean answerCallbackQuery(AnswerCallbackQueryRequest request) {
            this.lastAnswerCallbackQueryRequest = request;
            return true;
        }

        @Override
        public boolean answerInlineQuery(AnswerInlineQueryRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message sendInvoice(ru.tardyon.botframework.telegram.api.method.SendInvoiceRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean answerShippingQuery(ru.tardyon.botframework.telegram.api.method.AnswerShippingQueryRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean answerPreCheckoutQuery(ru.tardyon.botframework.telegram.api.method.AnswerPreCheckoutQueryRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setWebhook(SetWebhookRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setMyCommands(SetMyCommandsRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<BotCommand> getMyCommands(GetMyCommandsRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setChatMenuButton(SetChatMenuButtonRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MenuButton getChatMenuButton(GetChatMenuButtonRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TelegramFile getFile(GetFileRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message sendDocument(SendDocumentRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Message> sendMediaGroup(SendMediaGroupRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String buildFileDownloadUrl(String filePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] downloadFile(String filePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.nio.file.Path downloadFile(String filePath, java.nio.file.Path targetPath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteWebhook(DeleteWebhookRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public WebhookInfo getWebhookInfo() {
            throw new UnsupportedOperationException();
        }
    }
}
