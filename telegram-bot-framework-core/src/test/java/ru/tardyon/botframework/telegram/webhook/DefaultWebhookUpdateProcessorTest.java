package ru.tardyon.botframework.telegram.webhook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
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
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
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
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

class DefaultWebhookUpdateProcessorTest {

    @Test
    void dispatchesUpdateWhenSecretTokenIsValid() {
        CapturingDispatcher dispatcher = new CapturingDispatcher();
        DefaultWebhookUpdateProcessor processor = new DefaultWebhookUpdateProcessor(
            new ObjectMapper(),
            dispatcher,
            new NoopTelegramApiClient(),
            "secret-1"
        );

        String rawUpdate = """
            {
              "update_id": 9001,
              "message": {
                "message_id": 10,
                "date": 1710000000,
                "text": "ping",
                "chat": {
                  "id": 100,
                  "type": "private"
                }
              }
            }
            """;

        processor.process(
            rawUpdate,
            new WebhookRequestMetadata(Map.of(WebhookHeaders.TELEGRAM_SECRET_TOKEN_HEADER, List.of("secret-1")))
        );

        assertNotNull(dispatcher.lastContext);
        assertEquals(9001L, dispatcher.lastContext.getUpdate().updateId());
        assertEquals("ping", dispatcher.lastContext.getMessage().text());
    }

    @Test
    void rejectsInvalidSecretToken() {
        DefaultWebhookUpdateProcessor processor = new DefaultWebhookUpdateProcessor(
            new ObjectMapper(),
            context -> {
            },
            new NoopTelegramApiClient(),
            "expected-token"
        );

        String rawUpdate = """
            {
              "update_id": 1
            }
            """;

        assertThrows(
            WebhookSecurityException.class,
            () -> processor.process(
                rawUpdate,
                new WebhookRequestMetadata(Map.of(WebhookHeaders.TELEGRAM_SECRET_TOKEN_HEADER, List.of("wrong-token")))
            )
        );
    }

    private static final class CapturingDispatcher implements Dispatcher {

        private UpdateContext lastContext;

        @Override
        public void dispatch(UpdateContext updateContext) {
            this.lastContext = updateContext;
        }
    }

    private static final class NoopTelegramApiClient implements TelegramApiClient {

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
