package ru.tardyon.botframework.telegram.api;

import java.nio.file.Path;
import java.util.List;
import ru.tardyon.botframework.telegram.api.method.AnswerCallbackQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerInlineQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerPreCheckoutQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerShippingQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerWebAppQueryRequest;
import ru.tardyon.botframework.telegram.api.method.ApproveChatJoinRequestRequest;
import ru.tardyon.botframework.telegram.api.method.BanChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.CopyMessageRequest;
import ru.tardyon.botframework.telegram.api.method.CopyMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.CreateChatInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.CreateForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMessageRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.DeclineChatJoinRequestRequest;
import ru.tardyon.botframework.telegram.api.method.EditChatInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.EditForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.EditGeneralForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageCaptionRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageMediaRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageTextRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatAdministratorsRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMemberCountRequest;
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
import ru.tardyon.botframework.telegram.api.method.DeleteChatPhotoRequest;
import ru.tardyon.botframework.telegram.api.method.CreateChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.ConvertGiftToStarsRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.EditChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.EditUserStarSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.ForwardMessageRequest;
import ru.tardyon.botframework.telegram.api.method.ForwardMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.ForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.GiftPremiumSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.GeneralForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest;
import ru.tardyon.botframework.telegram.api.method.RefundStarPaymentRequest;
import ru.tardyon.botframework.telegram.api.method.RevokeChatInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.PinChatMessageRequest;
import ru.tardyon.botframework.telegram.api.method.PromoteChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.RestrictChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.SendAnimationRequest;
import ru.tardyon.botframework.telegram.api.method.SendAudioRequest;
import ru.tardyon.botframework.telegram.api.method.SendGiftRequest;
import ru.tardyon.botframework.telegram.api.method.SendInvoiceRequest;
import ru.tardyon.botframework.telegram.api.method.SendChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.SendChatActionRequest;
import ru.tardyon.botframework.telegram.api.method.SendPaidMediaRequest;
import ru.tardyon.botframework.telegram.api.method.SendPollRequest;
import ru.tardyon.botframework.telegram.api.method.SendVideoRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatDescriptionRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatPermissionsRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatPhotoRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatTitleRequest;
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
import ru.tardyon.botframework.telegram.api.method.UnbanChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.UnpinAllChatMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.UnpinChatMessageRequest;
import ru.tardyon.botframework.telegram.api.method.UpgradeGiftRequest;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageResult;
import ru.tardyon.botframework.telegram.api.model.ChatFullInfo;
import ru.tardyon.botframework.telegram.api.model.ForumTopic;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.MessageId;
import ru.tardyon.botframework.telegram.api.model.ChatInviteLink;
import ru.tardyon.botframework.telegram.api.model.TelegramFile;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.WebhookInfo;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.chatmember.ChatMember;
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

    default Message forwardMessage(ForwardMessageRequest request) {
        throw new UnsupportedOperationException("forwardMessage is not implemented by this TelegramApiClient");
    }

    default List<Message> forwardMessages(ForwardMessagesRequest request) {
        throw new UnsupportedOperationException("forwardMessages is not implemented by this TelegramApiClient");
    }

    default MessageId copyMessage(CopyMessageRequest request) {
        throw new UnsupportedOperationException("copyMessage is not implemented by this TelegramApiClient");
    }

    default List<MessageId> copyMessages(CopyMessagesRequest request) {
        throw new UnsupportedOperationException("copyMessages is not implemented by this TelegramApiClient");
    }

    EditMessageTextResult editMessageText(EditMessageTextRequest request);

    EditMessageReplyMarkupResult editMessageReplyMarkup(EditMessageReplyMarkupRequest request);

    boolean deleteMessage(DeleteMessageRequest request);

    default boolean deleteMessages(DeleteMessagesRequest request) {
        throw new UnsupportedOperationException("deleteMessages is not implemented by this TelegramApiClient");
    }

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

    default ChatInviteLink createChatInviteLink(CreateChatInviteLinkRequest request) {
        throw new UnsupportedOperationException("createChatInviteLink is not implemented by this TelegramApiClient");
    }

    ChatInviteLink editChatSubscriptionInviteLink(EditChatSubscriptionInviteLinkRequest request);

    default ChatInviteLink editChatInviteLink(EditChatInviteLinkRequest request) {
        throw new UnsupportedOperationException("editChatInviteLink is not implemented by this TelegramApiClient");
    }

    default ChatInviteLink revokeChatInviteLink(RevokeChatInviteLinkRequest request) {
        throw new UnsupportedOperationException("revokeChatInviteLink is not implemented by this TelegramApiClient");
    }

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

    default boolean deleteMyCommands(DeleteMyCommandsRequest request) {
        throw new UnsupportedOperationException("deleteMyCommands is not implemented by this TelegramApiClient");
    }

    List<BotCommand> getMyCommands(GetMyCommandsRequest request);

    boolean setChatMenuButton(SetChatMenuButtonRequest request);

    MenuButton getChatMenuButton(GetChatMenuButtonRequest request);

    default ChatFullInfo getChat(GetChatRequest request) {
        throw new UnsupportedOperationException("getChat is not implemented by this TelegramApiClient");
    }

    default ChatMember getChatMember(GetChatMemberRequest request) {
        throw new UnsupportedOperationException("getChatMember is not implemented by this TelegramApiClient");
    }

    default List<ChatMember> getChatAdministrators(GetChatAdministratorsRequest request) {
        throw new UnsupportedOperationException("getChatAdministrators is not implemented by this TelegramApiClient");
    }

    default int getChatMemberCount(GetChatMemberCountRequest request) {
        throw new UnsupportedOperationException("getChatMemberCount is not implemented by this TelegramApiClient");
    }

    TelegramFile getFile(GetFileRequest request);

    Message sendDocument(SendDocumentRequest request);

    default Message sendPhoto(SendPhotoRequest request) {
        throw new UnsupportedOperationException("sendPhoto is not implemented by this TelegramApiClient");
    }

    default boolean sendChatAction(SendChatActionRequest request) {
        throw new UnsupportedOperationException("sendChatAction is not implemented by this TelegramApiClient");
    }

    default Message sendVideo(SendVideoRequest request) {
        throw new UnsupportedOperationException("sendVideo is not implemented by this TelegramApiClient");
    }

    default Message sendAudio(SendAudioRequest request) {
        throw new UnsupportedOperationException("sendAudio is not implemented by this TelegramApiClient");
    }

    default Message sendAnimation(SendAnimationRequest request) {
        throw new UnsupportedOperationException("sendAnimation is not implemented by this TelegramApiClient");
    }

    default Message sendPoll(SendPollRequest request) {
        throw new UnsupportedOperationException("sendPoll is not implemented by this TelegramApiClient");
    }

    List<Message> sendMediaGroup(SendMediaGroupRequest request);

    default EditMessageResult editMessageCaption(EditMessageCaptionRequest request) {
        throw new UnsupportedOperationException("editMessageCaption is not implemented by this TelegramApiClient");
    }

    default EditMessageResult editMessageMedia(EditMessageMediaRequest request) {
        throw new UnsupportedOperationException("editMessageMedia is not implemented by this TelegramApiClient");
    }

    default boolean approveChatJoinRequest(ApproveChatJoinRequestRequest request) {
        throw new UnsupportedOperationException("approveChatJoinRequest is not implemented by this TelegramApiClient");
    }

    default boolean declineChatJoinRequest(DeclineChatJoinRequestRequest request) {
        throw new UnsupportedOperationException("declineChatJoinRequest is not implemented by this TelegramApiClient");
    }

    default boolean pinChatMessage(PinChatMessageRequest request) {
        throw new UnsupportedOperationException("pinChatMessage is not implemented by this TelegramApiClient");
    }

    default boolean unpinChatMessage(UnpinChatMessageRequest request) {
        throw new UnsupportedOperationException("unpinChatMessage is not implemented by this TelegramApiClient");
    }

    default boolean unpinAllChatMessages(UnpinAllChatMessagesRequest request) {
        throw new UnsupportedOperationException("unpinAllChatMessages is not implemented by this TelegramApiClient");
    }

    default boolean setChatDescription(SetChatDescriptionRequest request) {
        throw new UnsupportedOperationException("setChatDescription is not implemented by this TelegramApiClient");
    }

    default boolean setChatTitle(SetChatTitleRequest request) {
        throw new UnsupportedOperationException("setChatTitle is not implemented by this TelegramApiClient");
    }

    default boolean setChatPhoto(SetChatPhotoRequest request) {
        throw new UnsupportedOperationException("setChatPhoto is not implemented by this TelegramApiClient");
    }

    default boolean deleteChatPhoto(DeleteChatPhotoRequest request) {
        throw new UnsupportedOperationException("deleteChatPhoto is not implemented by this TelegramApiClient");
    }

    default boolean banChatMember(BanChatMemberRequest request) {
        throw new UnsupportedOperationException("banChatMember is not implemented by this TelegramApiClient");
    }

    default boolean unbanChatMember(UnbanChatMemberRequest request) {
        throw new UnsupportedOperationException("unbanChatMember is not implemented by this TelegramApiClient");
    }

    default boolean restrictChatMember(RestrictChatMemberRequest request) {
        throw new UnsupportedOperationException("restrictChatMember is not implemented by this TelegramApiClient");
    }

    default boolean promoteChatMember(PromoteChatMemberRequest request) {
        throw new UnsupportedOperationException("promoteChatMember is not implemented by this TelegramApiClient");
    }

    default boolean setChatPermissions(SetChatPermissionsRequest request) {
        throw new UnsupportedOperationException("setChatPermissions is not implemented by this TelegramApiClient");
    }

    default ForumTopic createForumTopic(CreateForumTopicRequest request) {
        throw new UnsupportedOperationException("createForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean editForumTopic(EditForumTopicRequest request) {
        throw new UnsupportedOperationException("editForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean closeForumTopic(ForumTopicRequest request) {
        throw new UnsupportedOperationException("closeForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean reopenForumTopic(ForumTopicRequest request) {
        throw new UnsupportedOperationException("reopenForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean deleteForumTopic(ForumTopicRequest request) {
        throw new UnsupportedOperationException("deleteForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean unpinAllForumTopicMessages(ForumTopicRequest request) {
        throw new UnsupportedOperationException("unpinAllForumTopicMessages is not implemented by this TelegramApiClient");
    }

    default boolean editGeneralForumTopic(EditGeneralForumTopicRequest request) {
        throw new UnsupportedOperationException("editGeneralForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean closeGeneralForumTopic(GeneralForumTopicRequest request) {
        throw new UnsupportedOperationException("closeGeneralForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean reopenGeneralForumTopic(GeneralForumTopicRequest request) {
        throw new UnsupportedOperationException("reopenGeneralForumTopic is not implemented by this TelegramApiClient");
    }

    default boolean unpinAllGeneralForumTopicMessages(GeneralForumTopicRequest request) {
        throw new UnsupportedOperationException("unpinAllGeneralForumTopicMessages is not implemented by this TelegramApiClient");
    }

    String buildFileDownloadUrl(String filePath);

    byte[] downloadFile(String filePath);

    Path downloadFile(String filePath, Path targetPath);

    boolean setWebhook(SetWebhookRequest request);

    boolean deleteWebhook(DeleteWebhookRequest request);

    WebhookInfo getWebhookInfo();
}
