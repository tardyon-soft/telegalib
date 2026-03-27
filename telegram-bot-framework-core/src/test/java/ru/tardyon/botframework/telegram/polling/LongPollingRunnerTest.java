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
