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
import ru.tardyon.botframework.telegram.api.method.DeleteWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteBusinessMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessConnectionRequest;
import ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest;
import ru.tardyon.botframework.telegram.api.method.GetUserGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.SendInvoiceRequest;
import ru.tardyon.botframework.telegram.api.method.SendPaidMediaRequest;
import ru.tardyon.botframework.telegram.api.method.ReadBusinessMessageRequest;
import ru.tardyon.botframework.telegram.api.method.RefundStarPaymentRequest;
import ru.tardyon.botframework.telegram.api.method.EditUserStarSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.GiftPremiumSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.CreateChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.SendGiftRequest;
import ru.tardyon.botframework.telegram.api.method.SetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.SetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.SetWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.SendDocumentRequest;
import ru.tardyon.botframework.telegram.api.method.SavePreparedInlineMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.command.BotCommandScopeDefault;
import ru.tardyon.botframework.telegram.api.model.ChatInviteLink;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResult;
import ru.tardyon.botframework.telegram.api.model.inline.InlineQueryResultArticle;
import ru.tardyon.botframework.telegram.api.model.inline.InputTextMessageContent;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButtons;
import ru.tardyon.botframework.telegram.api.model.payment.LabeledPrice;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingOption;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMediaPhoto;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMediaVideo;
import ru.tardyon.botframework.telegram.api.model.payment.StarAmount;
import ru.tardyon.botframework.telegram.api.model.payment.StarTransactions;
import ru.tardyon.botframework.telegram.api.model.payment.Gifts;
import ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts;
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
                {"ok":true,"result":{"transactions":[{"id":"tx-1","amount":{"amount":25},"date":1710011111,"source":{"type":"other"},"receiver":{"type":"telegram_ads"}}],"next_offset":"off-2"}}
                """
        );
        DefaultTelegramApiClient client = new DefaultTelegramApiClient("token", "https://api.telegram.org", httpClient, objectMapper);

        StarTransactions result = client.getStarTransactions(new GetStarTransactionsRequest("off-1", 50));

        assertEquals("/bottoken/getStarTransactions", httpClient.lastRequest().uri().getPath());
        String body = new String(readBody(httpClient.lastRequest()), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"offset\":\"off-1\""));
        assertTrue(body.contains("\"limit\":50"));
        assertEquals(1, result.transactions().size());
        assertEquals("tx-1", result.transactions().getFirst().id());
        assertEquals("off-2", result.nextOffset());
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

        private final byte[] responseBody;
        private HttpRequest lastRequest;

        private RecordingHttpClient(String responseBody) {
            this.responseBody = responseBody.getBytes(StandardCharsets.UTF_8);
        }

        HttpRequest lastRequest() {
            return lastRequest;
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
