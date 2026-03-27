package ru.tardyon.botframework.telegram.polling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SendMediaGroupRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.TelegramFile;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.WebhookInfo;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.dispatcher.Dispatcher;

class LongPollingRunnerTest {

    @Test
    void advancesOffsetAfterSuccessfulProcessing() {
        FakeTelegramApiClient client = new FakeTelegramApiClient();
        client.enqueueUpdates(
            List.of(
                new Update(100L, null, null, null, null, null, null, null),
                new Update(101L, null, null, null, null, null, null, null)
            )
        );
        client.enqueueUpdates(List.of());

        LongPollingRunner runner = new LongPollingRunner(client, new LongPollingOptions(1, 100, null, 0));
        AtomicInteger processed = new AtomicInteger();
        Dispatcher dispatcher = ctx -> processed.incrementAndGet();

        runner.pollOnce(dispatcher);
        runner.pollOnce(dispatcher);

        assertEquals(2, processed.get());
        assertEquals(Arrays.asList(null, 102), client.requestedOffsets());
        assertEquals(102, runner.getNextOffset());
    }

    @Test
    void advancesOffsetByHighestUpdateId() {
        FakeTelegramApiClient client = new FakeTelegramApiClient();
        client.enqueueUpdates(
            List.of(
                new Update(200L, null, null, null, null, null, null, null),
                new Update(150L, null, null, null, null, null, null, null)
            )
        );

        LongPollingRunner runner = new LongPollingRunner(client, new LongPollingOptions(1, 100, null, 0));
        Dispatcher dispatcher = ctx -> {
        };

        runner.pollOnce(dispatcher);

        assertEquals(201, runner.getNextOffset());
    }

    @Test
    void stopsGracefullyWhenStopCalled() throws Exception {
        BlockingTelegramApiClient client = new BlockingTelegramApiClient();
        LongPollingRunner runner = new LongPollingRunner(client, new LongPollingOptions(1, 100, null, 0));
        Dispatcher dispatcher = ctx -> {
        };

        runner.start(dispatcher);
        assertTrue(client.awaitCallStarted(1, TimeUnit.SECONDS));

        runner.stop();

        assertFalse(runner.isRunning());
        assertTrue(client.awaitInterrupted(1, TimeUnit.SECONDS));
    }

    private static final class FakeTelegramApiClient implements TelegramApiClient {

        private final Queue<List<Update>> responses = new ArrayDeque<>();
        private final List<Integer> requestedOffsets = new ArrayList<>();

        void enqueueUpdates(List<Update> updates) {
            responses.add(updates);
        }

        List<Integer> requestedOffsets() {
            return requestedOffsets;
        }

        @Override
        public User getMe() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Update> getUpdates(GetUpdatesRequest request) {
            requestedOffsets.add(request.offset());
            return responses.isEmpty() ? List.of() : responses.remove();
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
        public ru.tardyon.botframework.telegram.api.model.story.Story postStory(
            ru.tardyon.botframework.telegram.api.method.PostStoryRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.story.Story editStory(
            ru.tardyon.botframework.telegram.api.method.EditStoryRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteStory(ru.tardyon.botframework.telegram.api.method.DeleteStoryRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.story.Story repostStory(
            ru.tardyon.botframework.telegram.api.method.RepostStoryRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message sendChecklist(ru.tardyon.botframework.telegram.api.method.SendChecklistRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message editMessageChecklist(ru.tardyon.botframework.telegram.api.method.EditMessageChecklistRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setBusinessAccountGiftSettings(
            ru.tardyon.botframework.telegram.api.method.SetBusinessAccountGiftSettingsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.StarAmount getBusinessAccountStarBalance(
            ru.tardyon.botframework.telegram.api.method.GetBusinessAccountStarBalanceRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean transferBusinessAccountStars(
            ru.tardyon.botframework.telegram.api.method.TransferBusinessAccountStarsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts getBusinessAccountGifts(
            ru.tardyon.botframework.telegram.api.method.GetBusinessAccountGiftsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean convertGiftToStars(ru.tardyon.botframework.telegram.api.method.ConvertGiftToStarsRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean upgradeGift(ru.tardyon.botframework.telegram.api.method.UpgradeGiftRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean transferGift(ru.tardyon.botframework.telegram.api.method.TransferGiftRequest request) {
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

    private static final class BlockingTelegramApiClient implements TelegramApiClient {

        private final CountDownLatch callStarted = new CountDownLatch(1);
        private final CountDownLatch interrupted = new CountDownLatch(1);

        boolean awaitCallStarted(long timeout, TimeUnit unit) throws InterruptedException {
            return callStarted.await(timeout, unit);
        }

        boolean awaitInterrupted(long timeout, TimeUnit unit) throws InterruptedException {
            return interrupted.await(timeout, unit);
        }

        @Override
        public User getMe() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Update> getUpdates(GetUpdatesRequest request) {
            callStarted.countDown();
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                interrupted.countDown();
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while polling", e);
            }
            return List.of();
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
        public ru.tardyon.botframework.telegram.api.model.story.Story postStory(
            ru.tardyon.botframework.telegram.api.method.PostStoryRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.story.Story editStory(
            ru.tardyon.botframework.telegram.api.method.EditStoryRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteStory(ru.tardyon.botframework.telegram.api.method.DeleteStoryRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.story.Story repostStory(
            ru.tardyon.botframework.telegram.api.method.RepostStoryRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message sendChecklist(ru.tardyon.botframework.telegram.api.method.SendChecklistRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message editMessageChecklist(ru.tardyon.botframework.telegram.api.method.EditMessageChecklistRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setBusinessAccountGiftSettings(
            ru.tardyon.botframework.telegram.api.method.SetBusinessAccountGiftSettingsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.StarAmount getBusinessAccountStarBalance(
            ru.tardyon.botframework.telegram.api.method.GetBusinessAccountStarBalanceRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean transferBusinessAccountStars(
            ru.tardyon.botframework.telegram.api.method.TransferBusinessAccountStarsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts getBusinessAccountGifts(
            ru.tardyon.botframework.telegram.api.method.GetBusinessAccountGiftsRequest request
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean convertGiftToStars(ru.tardyon.botframework.telegram.api.method.ConvertGiftToStarsRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean upgradeGift(ru.tardyon.botframework.telegram.api.method.UpgradeGiftRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean transferGift(ru.tardyon.botframework.telegram.api.method.TransferGiftRequest request) {
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
