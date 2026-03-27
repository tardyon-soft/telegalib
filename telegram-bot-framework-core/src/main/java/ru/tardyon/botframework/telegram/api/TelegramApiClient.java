package ru.tardyon.botframework.telegram.api;

import java.nio.file.Path;
import java.util.List;
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
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
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

public interface TelegramApiClient {

    User getMe();

    List<Update> getUpdates(GetUpdatesRequest request);

    Message sendMessage(SendMessageRequest request);

    EditMessageTextResult editMessageText(EditMessageTextRequest request);

    EditMessageReplyMarkupResult editMessageReplyMarkup(EditMessageReplyMarkupRequest request);

    boolean deleteMessage(DeleteMessageRequest request);

    boolean answerCallbackQuery(AnswerCallbackQueryRequest request);

    boolean answerInlineQuery(AnswerInlineQueryRequest request);

    boolean setMyCommands(SetMyCommandsRequest request);

    List<BotCommand> getMyCommands(GetMyCommandsRequest request);

    boolean setChatMenuButton(SetChatMenuButtonRequest request);

    MenuButton getChatMenuButton(GetChatMenuButtonRequest request);

    TelegramFile getFile(GetFileRequest request);

    Message sendDocument(SendDocumentRequest request);

    List<Message> sendMediaGroup(SendMediaGroupRequest request);

    String buildFileDownloadUrl(String filePath);

    byte[] downloadFile(String filePath);

    Path downloadFile(String filePath, Path targetPath);

    boolean setWebhook(SetWebhookRequest request);

    boolean deleteWebhook(DeleteWebhookRequest request);

    WebhookInfo getWebhookInfo();
}
