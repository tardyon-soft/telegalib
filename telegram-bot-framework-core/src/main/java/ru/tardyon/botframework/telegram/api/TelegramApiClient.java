package ru.tardyon.botframework.telegram.api;

import java.nio.file.Path;
import java.util.List;
import ru.tardyon.botframework.telegram.api.method.AnswerCallbackQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerInlineQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerPreCheckoutQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerShippingQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerWebAppQueryRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMessageRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageTextRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessConnectionRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountStarBalanceRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetFileRequest;
import ru.tardyon.botframework.telegram.api.method.GetUserGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.method.GetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.PostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.RepostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteStoryRequest;
import ru.tardyon.botframework.telegram.api.method.EditStoryRequest;
import ru.tardyon.botframework.telegram.api.method.ReadBusinessMessageRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteBusinessMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.CreateChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.ConvertGiftToStarsRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.EditChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.EditUserStarSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.GiftPremiumSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest;
import ru.tardyon.botframework.telegram.api.method.RefundStarPaymentRequest;
import ru.tardyon.botframework.telegram.api.method.SendGiftRequest;
import ru.tardyon.botframework.telegram.api.method.SendInvoiceRequest;
import ru.tardyon.botframework.telegram.api.method.SendChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.SendPaidMediaRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetBusinessAccountGiftSettingsRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SendMediaGroupRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SendPhotoRequest;
import ru.tardyon.botframework.telegram.api.method.SavePreparedInlineMessageRequest;
import ru.tardyon.botframework.telegram.api.method.TransferBusinessAccountStarsRequest;
import ru.tardyon.botframework.telegram.api.method.TransferGiftRequest;
import ru.tardyon.botframework.telegram.api.method.UpgradeGiftRequest;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.ChatInviteLink;
import ru.tardyon.botframework.telegram.api.model.TelegramFile;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.WebhookInfo;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.webapp.PreparedInlineMessage;
import ru.tardyon.botframework.telegram.api.model.webapp.SentWebAppMessage;
import ru.tardyon.botframework.telegram.api.model.payment.StarAmount;
import ru.tardyon.botframework.telegram.api.model.payment.StarTransactions;
import ru.tardyon.botframework.telegram.api.model.payment.Gifts;
import ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts;
import ru.tardyon.botframework.telegram.api.model.story.Story;

public interface TelegramApiClient {

    User getMe();

    List<Update> getUpdates(GetUpdatesRequest request);

    Message sendMessage(SendMessageRequest request);

    EditMessageTextResult editMessageText(EditMessageTextRequest request);

    EditMessageReplyMarkupResult editMessageReplyMarkup(EditMessageReplyMarkupRequest request);

    boolean deleteMessage(DeleteMessageRequest request);

    boolean answerCallbackQuery(AnswerCallbackQueryRequest request);

    boolean answerInlineQuery(AnswerInlineQueryRequest request);

    SentWebAppMessage answerWebAppQuery(AnswerWebAppQueryRequest request);

    PreparedInlineMessage savePreparedInlineMessage(SavePreparedInlineMessageRequest request);

    Message sendInvoice(SendInvoiceRequest request);

    Message sendPaidMedia(SendPaidMediaRequest request);

    boolean answerShippingQuery(AnswerShippingQueryRequest request);

    /**
     * The Telegram Bot API requires this method to be called within 10 seconds after receiving a pre_checkout_query update.
     */
    boolean answerPreCheckoutQuery(AnswerPreCheckoutQueryRequest request);

    Story postStory(PostStoryRequest request);

    Story editStory(EditStoryRequest request);

    boolean deleteStory(DeleteStoryRequest request);

    Story repostStory(RepostStoryRequest request);

    Message sendChecklist(SendChecklistRequest request);

    Message editMessageChecklist(EditMessageChecklistRequest request);

    Gifts getAvailableGifts();

    boolean sendGift(SendGiftRequest request);

    boolean giftPremiumSubscription(GiftPremiumSubscriptionRequest request);

    OwnedGifts getUserGifts(GetUserGiftsRequest request);

    OwnedGifts getChatGifts(GetChatGiftsRequest request);

    ChatInviteLink createChatSubscriptionInviteLink(CreateChatSubscriptionInviteLinkRequest request);

    ChatInviteLink editChatSubscriptionInviteLink(EditChatSubscriptionInviteLinkRequest request);

    boolean setBusinessAccountGiftSettings(SetBusinessAccountGiftSettingsRequest request);

    StarAmount getBusinessAccountStarBalance(GetBusinessAccountStarBalanceRequest request);

    boolean transferBusinessAccountStars(TransferBusinessAccountStarsRequest request);

    OwnedGifts getBusinessAccountGifts(GetBusinessAccountGiftsRequest request);

    boolean convertGiftToStars(ConvertGiftToStarsRequest request);

    boolean upgradeGift(UpgradeGiftRequest request);

    boolean transferGift(TransferGiftRequest request);

    StarAmount getMyStarBalance();

    StarTransactions getStarTransactions(GetStarTransactionsRequest request);

    boolean refundStarPayment(RefundStarPaymentRequest request);

    boolean editUserStarSubscription(EditUserStarSubscriptionRequest request);

    BusinessConnection getBusinessConnection(GetBusinessConnectionRequest request);

    boolean readBusinessMessage(ReadBusinessMessageRequest request);

    boolean deleteBusinessMessages(DeleteBusinessMessagesRequest request);

    boolean setMyCommands(SetMyCommandsRequest request);

    List<BotCommand> getMyCommands(GetMyCommandsRequest request);

    boolean setChatMenuButton(SetChatMenuButtonRequest request);

    MenuButton getChatMenuButton(GetChatMenuButtonRequest request);

    TelegramFile getFile(GetFileRequest request);

    Message sendDocument(SendDocumentRequest request);

    default Message sendPhoto(SendPhotoRequest request) {
        throw new UnsupportedOperationException("sendPhoto is not implemented by this TelegramApiClient");
    }

    List<Message> sendMediaGroup(SendMediaGroupRequest request);

    String buildFileDownloadUrl(String filePath);

    byte[] downloadFile(String filePath);

    Path downloadFile(String filePath, Path targetPath);

    boolean setWebhook(SetWebhookRequest request);

    boolean deleteWebhook(DeleteWebhookRequest request);

    WebhookInfo getWebhookInfo();
}
