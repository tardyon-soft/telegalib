package ru.tardyon.botframework.telegram.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.file.InputFile;
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
import ru.tardyon.botframework.telegram.api.method.DeleteChatPhotoRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteBusinessMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.DeclineChatJoinRequestRequest;
import ru.tardyon.botframework.telegram.api.method.EditChatInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.EditForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.EditGeneralForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageCaptionRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageMediaRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.EditChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.EditStoryRequest;
import ru.tardyon.botframework.telegram.api.method.ForwardMessageRequest;
import ru.tardyon.botframework.telegram.api.method.ForwardMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.ForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatAdministratorsRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMemberCountRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountStarBalanceRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessConnectionRequest;
import ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest;
import ru.tardyon.botframework.telegram.api.method.GetUserGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.PostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.RepostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteStoryRequest;
import ru.tardyon.botframework.telegram.api.method.SendInvoiceRequest;
import ru.tardyon.botframework.telegram.api.method.SendChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.SendChatActionRequest;
import ru.tardyon.botframework.telegram.api.method.SendAnimationRequest;
import ru.tardyon.botframework.telegram.api.method.SendAudioRequest;
import ru.tardyon.botframework.telegram.api.method.SendPaidMediaRequest;
import ru.tardyon.botframework.telegram.api.method.SendPollRequest;
import ru.tardyon.botframework.telegram.api.method.SendVideoRequest;
import ru.tardyon.botframework.telegram.api.method.ReadBusinessMessageRequest;
import ru.tardyon.botframework.telegram.api.method.RefundStarPaymentRequest;
import ru.tardyon.botframework.telegram.api.method.RevokeChatInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.PinChatMessageRequest;
import ru.tardyon.botframework.telegram.api.method.PromoteChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.RestrictChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.EditUserStarSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.GiftPremiumSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.GeneralForumTopicRequest;
import ru.tardyon.botframework.telegram.api.method.CreateChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.ConvertGiftToStarsRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.SetBusinessAccountGiftSettingsRequest;
import ru.tardyon.botframework.telegram.api.method.SendGiftRequest;
import ru.tardyon.botframework.telegram.api.method.TransferBusinessAccountStarsRequest;
import ru.tardyon.botframework.telegram.api.method.TransferGiftRequest;
import ru.tardyon.botframework.telegram.api.method.UnbanChatMemberRequest;
import ru.tardyon.botframework.telegram.api.method.UnpinAllChatMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.UnpinChatMessageRequest;
import ru.tardyon.botframework.telegram.api.method.UpgradeGiftRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatDescriptionRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatPermissionsRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatPhotoRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatTitleRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SavePreparedInlineMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.command.BotCommandScopeDefault;
import ru.tardyon.botframework.telegram.api.model.ChatInviteLink;
import ru.tardyon.botframework.telegram.api.model.ChatFullInfo;
import ru.tardyon.botframework.telegram.api.model.ChatPermissions;
import ru.tardyon.botframework.telegram.api.model.EditMessageResult;
import ru.tardyon.botframework.telegram.api.model.ForumTopic;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.MessageId;
import ru.tardyon.botframework.telegram.api.model.checklist.InputChecklist;
import ru.tardyon.botframework.telegram.api.model.checklist.InputChecklistTask;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultArticle;
import ru.tardyon.botframework.telegram.api.model.inline.InputTextMessageContent;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButtons;
import ru.tardyon.botframework.telegram.api.model.media.InputMediaPhoto;
import ru.tardyon.botframework.telegram.api.model.chatmember.ChatMember;
import ru.tardyon.botframework.telegram.api.model.payment.LabeledPrice;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingOption;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMediaPhoto;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMediaVideo;
import ru.tardyon.botframework.telegram.api.model.payment.AcceptedGiftTypes;
import ru.tardyon.botframework.telegram.api.model.payment.StarAmount;
import ru.tardyon.botframework.telegram.api.model.payment.StarTransactions;
import ru.tardyon.botframework.telegram.api.model.payment.Gifts;
import ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts;
import ru.tardyon.botframework.telegram.api.model.story.InputStoryContentPhoto;
import ru.tardyon.botframework.telegram.api.model.story.Story;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.webapp.PreparedInlineMessage;
import ru.tardyon.botframework.telegram.api.model.webapp.SentWebAppMessage;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

class DefaultTelegramApiClientStage2MethodsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void setWebhookUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.setWebhook(
            new SetWebhookRequest(
                "https://example.com/telegram/webhook",
                null,
                null,
                List.of("message", "callback_query"),
                true,
                "secret-token"
            )
        );

        assertTrue(result);
        assertEquals("/bottoken/setWebhook", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"url\":\"https://example.com/telegram/webhook\""));
        assertTrue(body.contains("\"allowed_updates\":[\"message\",\"callback_query\"]"));
        assertTrue(body.contains("\"drop_pending_updates\":true"));
        assertTrue(body.contains("\"secret_token\":\"secret-token\""));
    }

    @Test
    void deleteWebhookUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.deleteWebhook(new DeleteWebhookRequest(true));

        assertTrue(result);
        assertEquals("/bottoken/deleteWebhook", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"drop_pending_updates\":true"));
    }

    @Test
    void setMyCommandsSerializesCommandsAndScope() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.setMyCommands(
            new SetMyCommandsRequest(
                List.of(new BotCommand("start", "Start"), new BotCommand("help", "Help")),
                new BotCommandScopeDefault(),
                null
            )
        );

        assertTrue(result);
        assertEquals("/bottoken/setMyCommands", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"commands\""));
        assertTrue(body.contains("\"command\":\"start\""));
        assertTrue(body.contains("\"scope\":{\"type\":\"default\"}"));
    }

    @Test
    void editMessageReplyMarkupUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":true}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.editMessageReplyMarkup(
            EditMessageReplyMarkupRequest.forChatMessage(
                123L,
                10,
                Keyboards.inlineKeyboard().row(Keyboards.callbackButton("Menu", "menu:main")).build()
            )
        );

        assertEquals("/bottoken/editMessageReplyMarkup", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":123"));
        assertTrue(body.contains("\"message_id\":10"));
        assertTrue(body.contains("\"reply_markup\""));
        assertTrue(body.contains("\"callback_data\":\"menu:main\""));
    }

    @Test
    void sendDocumentWithFileIdUsesSendDocumentMethod() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":1,"chat":{"id":123,"type":"private"},"date":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendDocument(SendDocumentRequest.of(123L, InputFile.fileId("file-id-1")));

        assertEquals("/bottoken/sendDocument", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"document\":\"file-id-1\""));
    }

    @Test
    void answerInlineQueryUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        List<InlineQueryResult> results = List.of(
            new InlineQueryResultArticle("a1", "Title", InputTextMessageContent.of("Hello inline"))
        );
        boolean result = client.answerInlineQuery(new AnswerInlineQueryRequest("iq-1", results, 5, true, "n1", null));

        assertTrue(result);
        assertEquals("/bottoken/answerInlineQuery", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"inline_query_id\":\"iq-1\""));
        assertTrue(body.contains("\"results\""));
        assertTrue(body.contains("\"type\":\"article\""));
    }

    @Test
    void answerWebAppQueryUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"inline_message_id":"im-1"}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        SentWebAppMessage result = client.answerWebAppQuery(
            new AnswerWebAppQueryRequest(
                "waq-1",
                new InlineQueryResultArticle("a1", "Title", InputTextMessageContent.of("From WebApp"))
            )
        );

        assertEquals("im-1", result.inlineMessageId());
        assertEquals("/bottoken/answerWebAppQuery", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"web_app_query_id\":\"waq-1\""));
        assertTrue(body.contains("\"result\""));
        assertTrue(body.contains("\"type\":\"article\""));
    }

    @Test
    void savePreparedInlineMessageUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"id":"prepared-1","expiration_date":1710009999}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        PreparedInlineMessage result = client.savePreparedInlineMessage(
            new SavePreparedInlineMessageRequest(
                123L,
                new InlineQueryResultArticle("a1", "Title", InputTextMessageContent.of("Prepared")),
                true,
                false,
                true,
                false
            )
        );

        assertEquals("prepared-1", result.id());
        assertEquals(1710009999L, result.expirationDate());
        assertEquals("/bottoken/savePreparedInlineMessage", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"user_id\":123"));
        assertTrue(body.contains("\"allow_user_chats\":true"));
        assertTrue(body.contains("\"allow_group_chats\":true"));
    }

    @Test
    void sendInvoiceUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":101,"chat":{"id":123,"type":"private"},"date":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendInvoice(
            new SendInvoiceRequest(
                123L,
                "Pro plan",
                "Monthly subscription",
                "invoice:pro:monthly",
                "provider-token",
                "USD",
                List.of(new LabeledPrice("Pro", 499)),
                null,
                null,
                "start-pro",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        );

        assertEquals("/bottoken/sendInvoice", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":123"));
        assertTrue(body.contains("\"title\":\"Pro plan\""));
        assertTrue(body.contains("\"currency\":\"USD\""));
        assertTrue(body.contains("\"prices\":["));
    }

    @Test
    void sendPaidMediaSupportsPhotoAndVideo() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":102,"chat":{"id":123,"type":"private"},"date":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendPaidMedia(
            new SendPaidMediaRequest(
                null,
                123L,
                10,
                List.of(
                    InputPaidMediaPhoto.of(InputFile.fileId("photo-file-id")),
                    InputPaidMediaVideo.of(InputFile.fileId("video-file-id"))
                ),
                "paid:payload:1",
                "Paid media",
                null,
                null,
                true,
                true
            )
        );

        assertEquals("/bottoken/sendPaidMedia", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":123"));
        assertTrue(body.contains("\"star_count\":10"));
        assertTrue(body.contains("\"media\":["));
        assertTrue(body.contains("\"type\":\"photo\""));
        assertTrue(body.contains("\"type\":\"video\""));
    }

    @Test
    void getMyStarBalanceUsesExpectedMethodAndParsesResponse() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"amount":1000,"nanostar_amount":15}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        StarAmount result = client.getMyStarBalance();

        assertEquals("/bottoken/getMyStarBalance", httpClient.lastRequest().uri().getPath());
        assertEquals(1000, result.amount());
        assertEquals(15, result.nanostarAmount());
    }

    @Test
    void getStarTransactionsUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"transactions":[{"id":"tx-1","amount":{"amount":25},"date":1710011111,"source":{"type":"other"},"receiver":{"type":"telegram_ads"}}],"next_offset":2}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        StarTransactions result = client.getStarTransactions(new GetStarTransactionsRequest(1, 50));

        assertEquals("/bottoken/getStarTransactions", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"offset\":1"));
        assertTrue(body.contains("\"limit\":50"));
        assertEquals(1, result.transactions().size());
        assertEquals("tx-1", result.transactions().getFirst().id());
        assertEquals(2, result.nextOffset());
    }

    @Test
    void refundStarPaymentUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.refundStarPayment(new RefundStarPaymentRequest(777L, "tg-charge-1"));

        assertTrue(result);
        assertEquals("/bottoken/refundStarPayment", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"user_id\":777"));
        assertTrue(body.contains("\"telegram_payment_charge_id\":\"tg-charge-1\""));
    }

    @Test
    void editUserStarSubscriptionUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.editUserStarSubscription(
            new EditUserStarSubscriptionRequest(777L, "tg-charge-sub-1", true)
        );

        assertTrue(result);
        assertEquals("/bottoken/editUserStarSubscription", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"user_id\":777"));
        assertTrue(body.contains("\"telegram_payment_charge_id\":\"tg-charge-sub-1\""));
        assertTrue(body.contains("\"is_canceled\":true"));
    }

    @Test
    void getAvailableGiftsUsesExpectedMethodAndParsesResponse() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"gifts":[{"id":"gift-1","star_count":15}]}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        Gifts result = client.getAvailableGifts();

        assertEquals("/bottoken/getAvailableGifts", httpClient.lastRequest().uri().getPath());
        assertEquals(1, result.gifts().size());
        assertEquals("gift-1", result.gifts().getFirst().id());
    }

    @Test
    void sendGiftUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.sendGift(new SendGiftRequest(777L, null, "gift-1", true, "Happy", null, null));

        assertTrue(result);
        assertEquals("/bottoken/sendGift", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"user_id\":777"));
        assertTrue(body.contains("\"gift_id\":\"gift-1\""));
        assertTrue(body.contains("\"pay_for_upgrade\":true"));
    }

    @Test
    void giftPremiumSubscriptionUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.giftPremiumSubscription(
            new GiftPremiumSubscriptionRequest(777L, 3, 1000, "Enjoy", null, null)
        );

        assertTrue(result);
        assertEquals("/bottoken/giftPremiumSubscription", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"user_id\":777"));
        assertTrue(body.contains("\"month_count\":3"));
        assertTrue(body.contains("\"star_count\":1000"));
    }

    @Test
    void getUserGiftsUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"total_count":1,"gifts":[{"type":"regular","gift":{"id":"gift-1","star_count":15},"send_date":1710011111}]}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        OwnedGifts result = client.getUserGifts(new GetUserGiftsRequest(777L, null, null, null, null, null, true, "off-1", 10));

        assertEquals("/bottoken/getUserGifts", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"user_id\":777"));
        assertTrue(body.contains("\"sort_by_price\":true"));
        assertTrue(body.contains("\"offset\":\"off-1\""));
        assertEquals(1, result.gifts().size());
    }

    @Test
    void getChatGiftsUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"total_count":0,"gifts":[]}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        OwnedGifts result = client.getChatGifts(new GetChatGiftsRequest("@channel", true, false, null, null, null, null, null, null, null, 5));

        assertEquals("/bottoken/getChatGifts", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":\"@channel\""));
        assertTrue(body.contains("\"exclude_unsaved\":true"));
        assertTrue(body.contains("\"limit\":5"));
        assertEquals(0, result.gifts().size());
    }

    @Test
    void createChatSubscriptionInviteLinkUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"invite_link":"https://t.me/+abc","creates_join_request":false,"is_primary":false,"is_revoked":false,"subscription_period":2592000,"subscription_price":500}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        ChatInviteLink result = client.createChatSubscriptionInviteLink(
            new CreateChatSubscriptionInviteLinkRequest("@channel", "Pro", 2592000, 500)
        );

        assertEquals("/bottoken/createChatSubscriptionInviteLink", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":\"@channel\""));
        assertTrue(body.contains("\"subscription_period\":2592000"));
        assertTrue(body.contains("\"subscription_price\":500"));
        assertEquals(2592000, result.subscriptionPeriod());
    }

    @Test
    void editChatSubscriptionInviteLinkUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"invite_link":"https://t.me/+abc","creates_join_request":false,"is_primary":false,"is_revoked":false,"name":"Pro+"}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        ChatInviteLink result = client.editChatSubscriptionInviteLink(
            new EditChatSubscriptionInviteLinkRequest("@channel", "https://t.me/+abc", "Pro+")
        );

        assertEquals("/bottoken/editChatSubscriptionInviteLink", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"invite_link\":\"https://t.me/+abc\""));
        assertTrue(body.contains("\"name\":\"Pro+\""));
        assertEquals("Pro+", result.name());
    }

    @Test
    void postEditDeleteAndRepostStoryUseExpectedMethods() {
        RecordingHttpClient storyClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"id":7,"chat":{"id":123,"type":"private"}}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", storyClient, objectMapper);

        Story created = client.postStory(new PostStoryRequest("bc-1", InputStoryContentPhoto.of(InputFile.fileId("photo-id")), 86400, "s", null, null, null, true, true));
        assertEquals(7, created.id());
        assertEquals("/bottoken/postStory", storyClient.lastRequest().uri().getPath());
        String postBody = new String(readBody(storyClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(postBody.contains("name=\"business_connection_id\""));
        assertTrue(postBody.contains("name=\"content\""));

        Story edited = client.editStory(new EditStoryRequest("bc-1", 7, InputStoryContentPhoto.of(InputFile.fileId("photo-id")), "updated", null, null, null));
        assertEquals(7, edited.id());
        assertEquals("/bottoken/editStory", storyClient.lastRequest().uri().getPath());

        RecordingHttpClient repostClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"id":8,"chat":{"id":123,"type":"private"}}}
                """
        );
        DefaultTelegramApiClient repostApi = new DefaultTelegramApiClient("token", "https://api.telegram.org", repostClient, objectMapper);
        repostApi.repostStory(new RepostStoryRequest("bc-1", 123L, 7, 86400, null, null));
        assertEquals("/bottoken/repostStory", repostClient.lastRequest().uri().getPath());

        RecordingHttpClient deleteClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient deleteApi = new DefaultTelegramApiClient("token", "https://api.telegram.org", deleteClient, objectMapper);
        boolean deleteResult = deleteApi.deleteStory(new DeleteStoryRequest("bc-1", 7));
        assertTrue(deleteResult);
        assertEquals("/bottoken/deleteStory", deleteClient.lastRequest().uri().getPath());
    }

    @Test
    void sendAndEditChecklistUseExpectedMethods() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":201,"chat":{"id":123,"type":"private"},"date":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);
        InputChecklist checklist = new InputChecklist("Daily", null, null, List.of(new InputChecklistTask(1, "Task", null, null)), true, true);

        client.sendChecklist(new SendChecklistRequest("bc-1", 123L, checklist, true, true, null, null, null));
        assertEquals("/bottoken/sendChecklist", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"business_connection_id\":\"bc-1\""));
        assertTrue(body.contains("\"checklist\""));

        client.editMessageChecklist(new EditMessageChecklistRequest("bc-1", 123L, 201, checklist, null));
        assertEquals("/bottoken/editMessageChecklist", httpClient.lastRequest().uri().getPath());
    }

    @Test
    void businessGiftAndStarsMethodsUseExpectedMethods() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":true}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean settings = client.setBusinessAccountGiftSettings(
            new SetBusinessAccountGiftSettingsRequest("bc-1", true, new AcceptedGiftTypes(true, true, true, true, true))
        );
        assertTrue(settings);
        assertEquals("/bottoken/setBusinessAccountGiftSettings", httpClient.lastRequest().uri().getPath());

        client.transferBusinessAccountStars(new TransferBusinessAccountStarsRequest("bc-1", 25));
        assertEquals("/bottoken/transferBusinessAccountStars", httpClient.lastRequest().uri().getPath());

        client.convertGiftToStars(new ConvertGiftToStarsRequest("bc-1", "owned-1"));
        assertEquals("/bottoken/convertGiftToStars", httpClient.lastRequest().uri().getPath());

        client.upgradeGift(new UpgradeGiftRequest("bc-1", "owned-1", true, 0));
        assertEquals("/bottoken/upgradeGift", httpClient.lastRequest().uri().getPath());

        client.transferGift(new TransferGiftRequest("bc-1", "owned-1", 777L, 5));
        assertEquals("/bottoken/transferGift", httpClient.lastRequest().uri().getPath());
    }

    @Test
    void getBusinessAccountStarBalanceAndGiftsParseResponse() {
        RecordingHttpClient balanceClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"amount":321,"nanostar_amount":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", balanceClient, objectMapper);
        StarAmount balance = client.getBusinessAccountStarBalance(new GetBusinessAccountStarBalanceRequest("bc-1"));
        assertEquals(321, balance.amount());
        assertEquals("/bottoken/getBusinessAccountStarBalance", balanceClient.lastRequest().uri().getPath());

        RecordingHttpClient giftsClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"total_count":1,"gifts":[{"type":"regular","gift":{"id":"gift-1","star_count":15},"send_date":1710011111}]}}
                """
        );
        DefaultTelegramApiClient giftsApi = new DefaultTelegramApiClient("token", "https://api.telegram.org", giftsClient, objectMapper);
        OwnedGifts gifts = giftsApi.getBusinessAccountGifts(new GetBusinessAccountGiftsRequest("bc-1", null, null, null, null, null, null, null, null, null, 10));
        assertEquals(1, gifts.gifts().size());
        assertEquals("/bottoken/getBusinessAccountGifts", giftsClient.lastRequest().uri().getPath());
    }

    @Test
    void sendMessageSupportsBusinessConnectionId() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":1,"chat":{"id":123,"type":"private"},"date":1}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        client.sendMessage(new SendMessageRequest(123L, "hello", null, "bc-1"));

        assertEquals("/bottoken/sendMessage", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"business_connection_id\":\"bc-1\""));
    }

    @Test
    void answerShippingQueryUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.answerShippingQuery(
            new AnswerShippingQueryRequest(
                "ship-q-1",
                true,
                List.of(new ShippingOption("pickup", "Pickup", List.of(new LabeledPrice("Pickup", 0)))),
                null
            )
        );

        assertTrue(result);
        assertEquals("/bottoken/answerShippingQuery", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"shipping_query_id\":\"ship-q-1\""));
        assertTrue(body.contains("\"ok\":true"));
        assertTrue(body.contains("\"shipping_options\""));
    }

    @Test
    void answerPreCheckoutQueryUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.answerPreCheckoutQuery(new AnswerPreCheckoutQueryRequest("pcq-1", true, null));

        assertTrue(result);
        assertEquals("/bottoken/answerPreCheckoutQuery", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"pre_checkout_query_id\":\"pcq-1\""));
        assertTrue(body.contains("\"ok\":true"));
    }

    @Test
    void getBusinessConnectionUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"id":"bc-1","user":{"id":1,"is_bot":false,"first_name":"Ann"},"user_chat_id":9001,"date":1710000000,"is_enabled":true}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        BusinessConnection connection = client.getBusinessConnection(new GetBusinessConnectionRequest("bc-1"));

        assertEquals("bc-1", connection.id());
        assertEquals("/bottoken/getBusinessConnection", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"business_connection_id\":\"bc-1\""));
    }

    @Test
    void readBusinessMessageUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.readBusinessMessage(new ReadBusinessMessageRequest("bc-1", 123L, 44));

        assertTrue(result);
        assertEquals("/bottoken/readBusinessMessage", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"business_connection_id\":\"bc-1\""));
        assertTrue(body.contains("\"chat_id\":123"));
        assertTrue(body.contains("\"message_id\":44"));
    }

    @Test
    void deleteBusinessMessagesUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.deleteBusinessMessages(new DeleteBusinessMessagesRequest("bc-1", List.of(10, 11)));

        assertTrue(result);
        assertEquals("/bottoken/deleteBusinessMessages", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"business_connection_id\":\"bc-1\""));
        assertTrue(body.contains("\"message_ids\":[10,11]"));
    }

    @Test
    void setChatMenuButtonUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        boolean result = client.setChatMenuButton(new SetChatMenuButtonRequest(321L, MenuButtons.commandsButton()));

        assertTrue(result);
        assertEquals("/bottoken/setChatMenuButton", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":321"));
        assertTrue(body.contains("\"menu_button\":{\"type\":\"commands\"}"));
    }

    @Test
    void serializesInlineKeyboardWebAppButton() throws Exception {
        String json = objectMapper.writeValueAsString(
            ru.tardyon.botframework.telegram.api.model.markup.Keyboards.inlineKeyboard()
                .row(ru.tardyon.botframework.telegram.api.model.markup.Keyboards.webAppButton("Open App", new WebAppInfo("https://example.com/app")))
                .build()
        );
        assertTrue(json.contains("\"web_app\":{\"url\":\"https://example.com/app\"}"));
    }

    @Test
    void getChatMenuButtonParsesMenuButtonResult() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"type":"web_app","text":"Open","web_app":{"url":"https://example.com/app"}}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        MenuButton result = client.getChatMenuButton(new GetChatMenuButtonRequest(321L));

        assertEquals("/bottoken/getChatMenuButton", httpClient.lastRequest().uri().getPath());
        assertTrue(result instanceof ru.tardyon.botframework.telegram.api.model.menu.MenuButtonWebApp);
    }

    @Test
    void getChatUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{
                  "id":-1001234567890,
                  "type":"channel",
                  "title":"Demo Channel",
                  "username":"demo_channel",
                  "description":"Channel description",
                  "invite_link":"https://t.me/+abc",
                  "photo":{
                    "small_file_id":"small",
                    "small_file_unique_id":"small_unique",
                    "big_file_id":"big",
                    "big_file_unique_id":"big_unique"
                  },
                  "active_usernames":["demo_channel","demo_channel_alias"],
                  "linked_chat_id":-1009876543210,
                  "can_send_paid_media":true
                }}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        ChatFullInfo result = client.getChat(new GetChatRequest("@demo_channel"));

        assertEquals("/bottoken/getChat", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":\"@demo_channel\""));
        assertEquals(-1001234567890L, result.id());
        assertEquals("channel", result.type());
        assertEquals("Demo Channel", result.title());
        assertEquals("Channel description", result.description());
        assertEquals("https://t.me/+abc", result.inviteLink());
        assertEquals("big", result.photo().bigFileId());
        assertEquals(2, result.activeUsernames().size());
        assertEquals(-1009876543210L, result.linkedChatId());
        assertTrue(result.canSendPaidMedia());
    }

    @Test
    void editMessageCaptionUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":true}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        EditMessageResult result = client.editMessageCaption(EditMessageCaptionRequest.forChatMessage("@demo_channel", 11, "Updated"));

        assertEquals("/bottoken/editMessageCaption", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":\"@demo_channel\""));
        assertTrue(body.contains("\"message_id\":11"));
        assertTrue(body.contains("\"caption\":\"Updated\""));
        assertTrue(result.isSuccessful());
    }

    @Test
    void editMessageMediaUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":true}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        EditMessageResult result = client.editMessageMedia(
            EditMessageMediaRequest.forChatMessage(100L, 12, InputMediaPhoto.of(InputFile.fileId("photo-file-id")))
        );

        assertEquals("/bottoken/editMessageMedia", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":100"));
        assertTrue(body.contains("\"message_id\":12"));
        assertTrue(body.contains("\"media\":{\"type\":\"photo\",\"media\":\"photo-file-id\""));
        assertTrue(result.isSuccessful());
    }

    @Test
    void utilityChatMethodsUseExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        assertTrue(client.deleteMessages(new DeleteMessagesRequest(100L, List.of(1, 2))));
        assertEquals("/bottoken/deleteMessages", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"message_ids\":[1,2]"));

        assertTrue(client.sendChatAction(new SendChatActionRequest(null, 100L, null, "typing")));
        assertEquals("/bottoken/sendChatAction", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"action\":\"typing\""));

        assertTrue(client.deleteMyCommands(new DeleteMyCommandsRequest(null, "ru")));
        assertEquals("/bottoken/deleteMyCommands", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"language_code\":\"ru\""));
    }

    @Test
    void inviteLinkMethodsUseExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"invite_link":"https://t.me/+abc","is_primary":false,"is_revoked":false}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        ChatInviteLink created = client.createChatInviteLink(new CreateChatInviteLinkRequest("@demo_channel", "demo", null, 10, null));
        assertEquals("/bottoken/createChatInviteLink", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"member_limit\":10"));
        assertEquals("https://t.me/+abc", created.inviteLink());

        ChatInviteLink edited = client.editChatInviteLink(new EditChatInviteLinkRequest("@demo_channel", "https://t.me/+abc", "new", null, null, true));
        assertEquals("/bottoken/editChatInviteLink", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"creates_join_request\":true"));
        assertEquals("https://t.me/+abc", edited.inviteLink());

        ChatInviteLink revoked = client.revokeChatInviteLink(new RevokeChatInviteLinkRequest("@demo_channel", "https://t.me/+abc"));
        assertEquals("/bottoken/revokeChatInviteLink", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"invite_link\":\"https://t.me/+abc\""));
        assertEquals("https://t.me/+abc", revoked.inviteLink());
    }

    @Test
    void chatJoinRequestMethodsUseExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        assertTrue(client.approveChatJoinRequest(new ApproveChatJoinRequestRequest("@demo_channel", 42L)));
        assertEquals("/bottoken/approveChatJoinRequest", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"user_id\":42"));

        assertTrue(client.declineChatJoinRequest(new DeclineChatJoinRequestRequest("@demo_channel", 42L)));
        assertEquals("/bottoken/declineChatJoinRequest", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"chat_id\":\"@demo_channel\""));
    }

    @Test
    void copyAndForwardMethodsUseExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":77}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        MessageId copied = client.copyMessage(new CopyMessageRequest(100L, "@source", 7, null, null));
        assertEquals("/bottoken/copyMessage", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"from_chat_id\":\"@source\""));
        assertEquals(77, copied.messageId());

        httpClient.setDefaultJsonBody("""
            {"ok":true,"result":[{"message_id":77},{"message_id":78}]}
            """);
        List<MessageId> copiedMany = client.copyMessages(new CopyMessagesRequest(100L, "@source", List.of(7, 8)));
        assertEquals("/bottoken/copyMessages", httpClient.lastRequest().uri().getPath());
        assertEquals(2, copiedMany.size());

        httpClient.setDefaultJsonBody("""
            {"ok":true,"result":{"message_id":9,"date":1,"chat":{"id":100,"type":"private"}}}
            """);
        Message forwarded = client.forwardMessage(new ForwardMessageRequest(100L, "@source", 9));
        assertEquals("/bottoken/forwardMessage", httpClient.lastRequest().uri().getPath());
        assertEquals(9, forwarded.messageId());

        httpClient.setDefaultJsonBody("""
            {"ok":true,"result":[{"message_id":9,"date":1,"chat":{"id":100,"type":"private"}}]}
            """);
        List<Message> forwardedMany = client.forwardMessages(new ForwardMessagesRequest(100L, "@source", List.of(9)));
        assertEquals("/bottoken/forwardMessages", httpClient.lastRequest().uri().getPath());
        assertEquals(1, forwardedMany.size());
    }

    @Test
    void mediaPollAndChatAdminMethodsUseExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_id":10,"date":1,"chat":{"id":100,"type":"private"}}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        assertEquals(10, client.sendVideo(SendVideoRequest.of(100L, InputFile.fileId("video-id"))).messageId());
        assertEquals("/bottoken/sendVideo", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"video\":\"video-id\""));

        client.sendAudio(SendAudioRequest.of(100L, InputFile.fileId("audio-id")));
        assertEquals("/bottoken/sendAudio", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"audio\":\"audio-id\""));

        client.sendAnimation(SendAnimationRequest.of(100L, InputFile.fileId("anim-id")));
        assertEquals("/bottoken/sendAnimation", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"animation\":\"anim-id\""));

        client.sendPoll(new SendPollRequest(100L, "Question?", List.of("A", "B"), true, null, null, null));
        assertEquals("/bottoken/sendPoll", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"options\":[\"A\",\"B\"]"));

        httpClient.setDefaultJsonBody(okTrueResponse());
        assertTrue(client.pinChatMessage(new PinChatMessageRequest(null, "@demo_channel", 10, true)));
        assertEquals("/bottoken/pinChatMessage", httpClient.lastRequest().uri().getPath());

        assertTrue(client.setChatDescription(new SetChatDescriptionRequest("@demo_channel", "Description")));
        assertEquals("/bottoken/setChatDescription", httpClient.lastRequest().uri().getPath());
    }

    @Test
    void chatAdministrationMethodsUseExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(okTrueResponse());
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);
        ChatPermissions permissions = new ChatPermissions(true, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertTrue(client.unpinChatMessage(new UnpinChatMessageRequest(null, "@demo_channel", 10)));
        assertEquals("/bottoken/unpinChatMessage", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"message_id\":10"));

        assertTrue(client.unpinAllChatMessages(new UnpinAllChatMessagesRequest("@demo_channel")));
        assertEquals("/bottoken/unpinAllChatMessages", httpClient.lastRequest().uri().getPath());

        assertTrue(client.setChatTitle(new SetChatTitleRequest("@demo_channel", "New title")));
        assertEquals("/bottoken/setChatTitle", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"title\":\"New title\""));

        assertTrue(client.setChatPhoto(new SetChatPhotoRequest("@demo_channel", InputFile.fileId("photo-id"))));
        assertEquals("/bottoken/setChatPhoto", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"photo\":\"photo-id\""));

        assertTrue(client.deleteChatPhoto(new DeleteChatPhotoRequest("@demo_channel")));
        assertEquals("/bottoken/deleteChatPhoto", httpClient.lastRequest().uri().getPath());

        assertTrue(client.banChatMember(new BanChatMemberRequest("@demo_channel", 42L, null, true)));
        assertEquals("/bottoken/banChatMember", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"revoke_messages\":true"));

        assertTrue(client.unbanChatMember(new UnbanChatMemberRequest("@demo_channel", 42L, true)));
        assertEquals("/bottoken/unbanChatMember", httpClient.lastRequest().uri().getPath());

        assertTrue(client.restrictChatMember(new RestrictChatMemberRequest("@demo_channel", 42L, permissions, true, null)));
        assertEquals("/bottoken/restrictChatMember", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"can_send_messages\":true"));

        assertTrue(client.promoteChatMember(new PromoteChatMemberRequest("@demo_channel", 42L, null, true, true, null, null, null, null, null, null, null, null, true, null, null, null, null, null)));
        assertEquals("/bottoken/promoteChatMember", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"can_manage_chat\":true"));

        assertTrue(client.setChatPermissions(new SetChatPermissionsRequest("@demo_channel", permissions, true)));
        assertEquals("/bottoken/setChatPermissions", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"use_independent_chat_permissions\":true"));
    }

    @Test
    void forumTopicMethodsUseExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"message_thread_id":77,"name":"Topic","icon_color":7322096}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        ForumTopic topic = client.createForumTopic(new CreateForumTopicRequest("@forum", "Topic", 7322096, null));
        assertEquals("/bottoken/createForumTopic", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"name\":\"Topic\""));
        assertEquals(77, topic.messageThreadId());

        httpClient.setDefaultJsonBody(okTrueResponse());
        assertTrue(client.editForumTopic(new EditForumTopicRequest("@forum", 77, "Renamed", null)));
        assertEquals("/bottoken/editForumTopic", httpClient.lastRequest().uri().getPath());
        assertTrue(new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8).contains("\"message_thread_id\":77"));

        assertTrue(client.closeForumTopic(new ForumTopicRequest("@forum", 77)));
        assertEquals("/bottoken/closeForumTopic", httpClient.lastRequest().uri().getPath());

        assertTrue(client.reopenForumTopic(new ForumTopicRequest("@forum", 77)));
        assertEquals("/bottoken/reopenForumTopic", httpClient.lastRequest().uri().getPath());

        assertTrue(client.deleteForumTopic(new ForumTopicRequest("@forum", 77)));
        assertEquals("/bottoken/deleteForumTopic", httpClient.lastRequest().uri().getPath());

        assertTrue(client.unpinAllForumTopicMessages(new ForumTopicRequest("@forum", 77)));
        assertEquals("/bottoken/unpinAllForumTopicMessages", httpClient.lastRequest().uri().getPath());

        assertTrue(client.editGeneralForumTopic(new EditGeneralForumTopicRequest("@forum", "General")));
        assertEquals("/bottoken/editGeneralForumTopic", httpClient.lastRequest().uri().getPath());

        assertTrue(client.closeGeneralForumTopic(new GeneralForumTopicRequest("@forum")));
        assertEquals("/bottoken/closeGeneralForumTopic", httpClient.lastRequest().uri().getPath());

        assertTrue(client.reopenGeneralForumTopic(new GeneralForumTopicRequest("@forum")));
        assertEquals("/bottoken/reopenGeneralForumTopic", httpClient.lastRequest().uri().getPath());

        assertTrue(client.unpinAllGeneralForumTopicMessages(new GeneralForumTopicRequest("@forum")));
        assertEquals("/bottoken/unpinAllGeneralForumTopicMessages", httpClient.lastRequest().uri().getPath());
    }

    @Test
    void getChatMemberUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":{"status":"member","user":{"id":42,"is_bot":false,"first_name":"U"}}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        ChatMember result = client.getChatMember(new GetChatMemberRequest("@demo_channel", 42L));

        assertEquals("/bottoken/getChatMember", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":\"@demo_channel\""));
        assertTrue(body.contains("\"user_id\":42"));
        assertEquals("member", result.status());
    }

    @Test
    void getChatAdministratorsUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":[
                  {"status":"creator","is_anonymous":false,"user":{"id":1,"is_bot":false,"first_name":"Owner"}},
                  {"status":"administrator","can_be_edited":true,"is_anonymous":false,"user":{"id":2,"is_bot":false,"first_name":"Admin"}}
                ]}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        List<ChatMember> result = client.getChatAdministrators(new GetChatAdministratorsRequest(-1001234567890L));

        assertEquals("/bottoken/getChatAdministrators", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":-1001234567890"));
        assertEquals(2, result.size());
        assertEquals("creator", result.get(0).status());
        assertEquals("administrator", result.get(1).status());
    }

    @Test
    void getChatMemberCountUsesExpectedMethodAndPayload() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
            """
                {"ok":true,"result":1337}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        int result = client.getChatMemberCount(new GetChatMemberCountRequest("@demo_channel"));

        assertEquals("/bottoken/getChatMemberCount", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"chat_id\":\"@demo_channel\""));
        assertEquals(1337, result);
    }

    private static String okTrueResponse() {
        return """
            {"ok":true,"result":true}
            """;
    }

    private static byte[] readBody(HttpRequest request) {
        HttpRequest.BodyPublisher publisher = request.bodyPublisher().orElseThrow();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                byte[] chunk = new byte[item.remaining()];
                item.get(chunk);
                out.writeBytes(chunk);
            }

            @Override
            public void onError(Throwable throwable) {
                throw new IllegalStateException(throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        return out.toByteArray();
    }

    private static final class RecordingHttpClient extends HttpClient {

        private byte[] responseBody;
        private HttpRequest lastRequest;

        private RecordingHttpClient(String responseBody) {
            this.responseBody = responseBody.getBytes(StandardCharsets.UTF_8);
        }

        HttpRequest lastRequest() {
            return lastRequest;
        }

        void setDefaultJsonBody(String responseBody) {
            this.responseBody = responseBody.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            this.lastRequest = request;
            HttpResponse.ResponseInfo responseInfo = new HttpResponse.ResponseInfo() {
                @Override
                public int statusCode() {
                    return 200;
                }

                @Override
                public HttpHeaders headers() {
                    return HttpHeaders.of(Map.of(), (a, b) -> true);
                }

                @Override
                public Version version() {
                    return Version.HTTP_1_1;
                }
            };
            HttpResponse.BodySubscriber<T> subscriber = responseBodyHandler.apply(responseInfo);
            subscriber.onSubscribe(new Flow.Subscription() {
                private boolean done;

                @Override
                public void request(long n) {
                    if (done) {
                        return;
                    }
                    done = true;
                    subscriber.onNext(List.of(ByteBuffer.wrap(responseBody)));
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {
                    done = true;
                }
            });
            T body = subscriber.getBody().toCompletableFuture().join();
            return new StubHttpResponse<>(request, body);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
            HttpRequest request,
            HttpResponse.BodyHandler<T> responseBodyHandler,
            HttpResponse.PushPromiseHandler<T> pushPromiseHandler
        ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<java.net.CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.empty();
        }

        @Override
        public Redirect followRedirects() {
            return Redirect.NEVER;
        }

        @Override
        public Optional<java.net.ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public javax.net.ssl.SSLContext sslContext() {
            return null;
        }

        @Override
        public javax.net.ssl.SSLParameters sslParameters() {
            return null;
        }

        @Override
        public Optional<java.net.Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public Version version() {
            return Version.HTTP_1_1;
        }

        @Override
        public Optional<java.util.concurrent.Executor> executor() {
            return Optional.empty();
        }
    }

    private record StubHttpResponse<T>(HttpRequest request, T body) implements HttpResponse<T> {
        @Override
        public int statusCode() {
            return 200;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(Map.of(), (a, b) -> true);
        }

        @Override
        public T body() {
            return body;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return request.uri();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
