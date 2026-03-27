package ru.tardyon.botframework.telegram.spring.boot.lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
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
import ru.tardyon.botframework.telegram.api.method.GetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SendMediaGroupRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.TelegramFile;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.WebhookInfo;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.bot.TelegramBot;
import ru.tardyon.botframework.telegram.spring.boot.properties.TelegramBotFrameworkProperties;

class TelegramBotLifecycleTest {

    @Test
    void startsAndStopsPollingWhenPollingModeEnabled() {
        SpyTelegramBot bot = new SpyTelegramBot();
        SpyTelegramApiClient apiClient = new SpyTelegramApiClient();
        TelegramBotFrameworkProperties properties = new TelegramBotFrameworkProperties();
        properties.setMode(TelegramBotFrameworkProperties.Mode.POLLING);
        properties.getPolling().setEnabled(true);

        TelegramBotLifecycle lifecycle = new TelegramBotLifecycle(bot, apiClient, properties);
        lifecycle.start();
        lifecycle.stop();

        assertEquals(1, bot.startPollingCalls);
        assertEquals(0, bot.startWebhookCalls);
        assertEquals(1, bot.stopCalls);
        assertEquals(0, apiClient.setWebhookCalls);
        assertFalse(lifecycle.isRunning());
    }

    @Test
    void startsWebhookAndRegistersSetWebhookWhenConfigured() {
        SpyTelegramBot bot = new SpyTelegramBot();
        SpyTelegramApiClient apiClient = new SpyTelegramApiClient();
        TelegramBotFrameworkProperties properties = new TelegramBotFrameworkProperties();
        properties.setMode(TelegramBotFrameworkProperties.Mode.WEBHOOK);
        properties.getWebhook().setEnabled(true);
        properties.getWebhook().setPath("/telegram/webhook");
        properties.getWebhook().setPublicUrl("https://example.com");
        properties.getWebhook().setSecretToken("secret-1");
        properties.getWebhook().setDropPendingUpdates(true);
        properties.getPolling().setAllowedUpdates(List.of("message", "callback_query"));

        TelegramBotLifecycle lifecycle = new TelegramBotLifecycle(bot, apiClient, properties);
        lifecycle.start();

        assertEquals(0, bot.startPollingCalls);
        assertEquals(1, bot.startWebhookCalls);
        assertEquals(1, apiClient.setWebhookCalls);
        assertEquals("https://example.com/telegram/webhook", apiClient.lastSetWebhookRequest.url());
        assertEquals("secret-1", apiClient.lastSetWebhookRequest.secretToken());
        assertEquals(true, apiClient.lastSetWebhookRequest.dropPendingUpdates());
        assertEquals(List.of("message", "callback_query"), apiClient.lastSetWebhookRequest.allowedUpdates());
        assertTrue(lifecycle.isRunning());
    }

    @Test
    void webhookModeWithoutPublicUrlDoesNotCallSetWebhook() {
        SpyTelegramBot bot = new SpyTelegramBot();
        SpyTelegramApiClient apiClient = new SpyTelegramApiClient();
        TelegramBotFrameworkProperties properties = new TelegramBotFrameworkProperties();
        properties.setMode(TelegramBotFrameworkProperties.Mode.WEBHOOK);
        properties.getWebhook().setEnabled(true);
        properties.getWebhook().setPath("/hook");

        TelegramBotLifecycle lifecycle = new TelegramBotLifecycle(bot, apiClient, properties);
        lifecycle.start();

        assertEquals(1, bot.startWebhookCalls);
        assertEquals(0, apiClient.setWebhookCalls);
    }

    @Test
    void doesNotStartWhenModeDisabledByFlags() {
        SpyTelegramBot bot = new SpyTelegramBot();
        SpyTelegramApiClient apiClient = new SpyTelegramApiClient();
        TelegramBotFrameworkProperties properties = new TelegramBotFrameworkProperties();
        properties.setMode(TelegramBotFrameworkProperties.Mode.POLLING);
        properties.getPolling().setEnabled(false);

        TelegramBotLifecycle lifecycle = new TelegramBotLifecycle(bot, apiClient, properties);
        lifecycle.start();

        assertEquals(0, bot.startPollingCalls);
        assertEquals(0, bot.startWebhookCalls);
        assertFalse(lifecycle.isRunning());
        assertFalse(lifecycle.isAutoStartup());
    }

    private static final class SpyTelegramBot implements TelegramBot {
        private int startPollingCalls;
        private int startWebhookCalls;
        private int stopCalls;

        @Override
        public void startPolling() {
            startPollingCalls++;
        }

        @Override
        public void startWebhook() {
            startWebhookCalls++;
        }

        @Override
        public void stop() {
            stopCalls++;
        }
    }

    private static final class SpyTelegramApiClient implements TelegramApiClient {
        private int setWebhookCalls;
        private SetWebhookRequest lastSetWebhookRequest;

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
            throw new UnsupportedOperationException();
        }

        @Override
        public EditMessageTextResult editMessageText(EditMessageTextRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public EditMessageReplyMarkupResult editMessageReplyMarkup(EditMessageReplyMarkupRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteMessage(DeleteMessageRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean answerCallbackQuery(AnswerCallbackQueryRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean answerInlineQuery(AnswerInlineQueryRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.webapp.SentWebAppMessage answerWebAppQuery(
            ru.tardyon.botframework.telegram.api.method.AnswerWebAppQueryRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.webapp.PreparedInlineMessage savePreparedInlineMessage(
            ru.tardyon.botframework.telegram.api.method.SavePreparedInlineMessageRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message sendInvoice(ru.tardyon.botframework.telegram.api.method.SendInvoiceRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message sendPaidMedia(ru.tardyon.botframework.telegram.api.method.SendPaidMediaRequest request) {
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
        public ru.tardyon.botframework.telegram.api.model.payment.Gifts getAvailableGifts() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean sendGift(ru.tardyon.botframework.telegram.api.method.SendGiftRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean giftPremiumSubscription(ru.tardyon.botframework.telegram.api.method.GiftPremiumSubscriptionRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts getUserGifts(
            ru.tardyon.botframework.telegram.api.method.GetUserGiftsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts getChatGifts(
            ru.tardyon.botframework.telegram.api.method.GetChatGiftsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.ChatInviteLink createChatSubscriptionInviteLink(
            ru.tardyon.botframework.telegram.api.method.CreateChatSubscriptionInviteLinkRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.ChatInviteLink editChatSubscriptionInviteLink(
            ru.tardyon.botframework.telegram.api.method.EditChatSubscriptionInviteLinkRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.StarAmount getMyStarBalance() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.StarTransactions getStarTransactions(
            ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean refundStarPayment(ru.tardyon.botframework.telegram.api.method.RefundStarPaymentRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean editUserStarSubscription(ru.tardyon.botframework.telegram.api.method.EditUserStarSubscriptionRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.business.BusinessConnection getBusinessConnection(
            ru.tardyon.botframework.telegram.api.method.GetBusinessConnectionRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean readBusinessMessage(ru.tardyon.botframework.telegram.api.method.ReadBusinessMessageRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteBusinessMessages(ru.tardyon.botframework.telegram.api.method.DeleteBusinessMessagesRequest request) {
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
        public Path downloadFile(String filePath, Path targetPath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setWebhook(SetWebhookRequest request) {
            this.setWebhookCalls++;
            this.lastSetWebhookRequest = request;
            return true;
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
