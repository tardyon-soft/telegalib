package ru.tardyon.botframework.telegram.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.method.AnswerCallbackQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerInlineQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerPreCheckoutQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerShippingQueryRequest;
import ru.tardyon.botframework.telegram.api.method.AnswerWebAppQueryRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteBusinessMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteMessageRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteWebhookRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageChecklistRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageTextRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatMenuButtonRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessAccountStarBalanceRequest;
import ru.tardyon.botframework.telegram.api.method.GetBusinessConnectionRequest;
import ru.tardyon.botframework.telegram.api.method.GetChatGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.GetFileRequest;
import ru.tardyon.botframework.telegram.api.method.GetUpdatesRequest;
import ru.tardyon.botframework.telegram.api.method.GetMyCommandsRequest;
import ru.tardyon.botframework.telegram.api.method.GetUserGiftsRequest;
import ru.tardyon.botframework.telegram.api.method.PostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.RepostStoryRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteStoryRequest;
import ru.tardyon.botframework.telegram.api.method.EditStoryRequest;
import ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest;
import ru.tardyon.botframework.telegram.api.method.ReadBusinessMessageRequest;
import ru.tardyon.botframework.telegram.api.method.RefundStarPaymentRequest;
import ru.tardyon.botframework.telegram.api.method.EditUserStarSubscriptionRequest;
import ru.tardyon.botframework.telegram.api.method.CreateChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.ConvertGiftToStarsRequest;
import ru.tardyon.botframework.telegram.api.method.EditChatSubscriptionInviteLinkRequest;
import ru.tardyon.botframework.telegram.api.method.GiftPremiumSubscriptionRequest;
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
import ru.tardyon.botframework.telegram.api.method.SavePreparedInlineMessageRequest;
import ru.tardyon.botframework.telegram.api.method.TransferBusinessAccountStarsRequest;
import ru.tardyon.botframework.telegram.api.method.TransferGiftRequest;
import ru.tardyon.botframework.telegram.api.method.UpgradeGiftRequest;
import ru.tardyon.botframework.telegram.api.file.InputFile;
import ru.tardyon.botframework.telegram.api.file.InputFileBytes;
import ru.tardyon.botframework.telegram.api.file.InputFilePath;
import ru.tardyon.botframework.telegram.api.file.InputFileReference;
import ru.tardyon.botframework.telegram.api.file.InputFileStream;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.ChatInviteLink;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.TelegramFile;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.WebhookInfo;
import ru.tardyon.botframework.telegram.api.model.MessageEntity;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.command.BotCommand;
import ru.tardyon.botframework.telegram.api.model.menu.MenuButton;
import ru.tardyon.botframework.telegram.api.model.media.InputMedia;
import ru.tardyon.botframework.telegram.api.model.payment.InputPaidMedia;
import ru.tardyon.botframework.telegram.api.model.story.InputStoryContent;
import ru.tardyon.botframework.telegram.api.model.story.Story;
import ru.tardyon.botframework.telegram.api.model.payment.StarAmount;
import ru.tardyon.botframework.telegram.api.model.payment.StarTransactions;
import ru.tardyon.botframework.telegram.api.model.payment.Gifts;
import ru.tardyon.botframework.telegram.api.model.payment.OwnedGifts;
import ru.tardyon.botframework.telegram.api.model.webapp.PreparedInlineMessage;
import ru.tardyon.botframework.telegram.api.model.webapp.SentWebAppMessage;
import ru.tardyon.botframework.telegram.api.transport.MultipartFormData;
import ru.tardyon.botframework.telegram.api.transport.TelegramApiResponse;
import ru.tardyon.botframework.telegram.exception.TelegramApiException;

public class DefaultTelegramApiClient implements TelegramApiClient {

    private static final String DEFAULT_BASE_URL = "https://api.telegram.org";
    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";

    private final String botToken;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public DefaultTelegramApiClient(String botToken) {
        this(botToken, DEFAULT_BASE_URL, HttpClient.newHttpClient(), new ObjectMapper());
    }

    public DefaultTelegramApiClient(String botToken, HttpClient httpClient, ObjectMapper objectMapper) {
        this(botToken, DEFAULT_BASE_URL, httpClient, objectMapper);
    }

    public DefaultTelegramApiClient(String botToken, String baseUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.botToken = Objects.requireNonNull(botToken, "botToken must not be null");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null").copy();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public User getMe() {
        return invoke("getMe", null, objectMapper.getTypeFactory().constructType(User.class));
    }

    @Override
    public List<Update> getUpdates(GetUpdatesRequest request) {
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Update.class);
        return invoke("getUpdates", request == null ? new GetUpdatesRequest() : request, listType);
    }

    @Override
    public Message sendMessage(SendMessageRequest request) {
        return invoke("sendMessage", requireRequest(request), objectMapper.getTypeFactory().constructType(Message.class));
    }

    @Override
    public EditMessageTextResult editMessageText(EditMessageTextRequest request) {
        return invoke("editMessageText", requireRequest(request), objectMapper.getTypeFactory().constructType(EditMessageTextResult.class));
    }

    @Override
    public EditMessageReplyMarkupResult editMessageReplyMarkup(EditMessageReplyMarkupRequest request) {
        return invoke(
            "editMessageReplyMarkup",
            requireRequest(request),
            objectMapper.getTypeFactory().constructType(EditMessageReplyMarkupResult.class)
        );
    }

    @Override
    public boolean deleteMessage(DeleteMessageRequest request) {
        Boolean result = invoke("deleteMessage", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean answerCallbackQuery(AnswerCallbackQueryRequest request) {
        Boolean result = invoke("answerCallbackQuery", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean answerInlineQuery(AnswerInlineQueryRequest request) {
        Boolean result = invoke("answerInlineQuery", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public SentWebAppMessage answerWebAppQuery(AnswerWebAppQueryRequest request) {
        return invoke("answerWebAppQuery", requireRequest(request), objectMapper.getTypeFactory().constructType(SentWebAppMessage.class));
    }

    @Override
    public PreparedInlineMessage savePreparedInlineMessage(SavePreparedInlineMessageRequest request) {
        return invoke(
            "savePreparedInlineMessage",
            requireRequest(request),
            objectMapper.getTypeFactory().constructType(PreparedInlineMessage.class)
        );
    }

    @Override
    public Message sendInvoice(SendInvoiceRequest request) {
        return invoke("sendInvoice", requireRequest(request), objectMapper.getTypeFactory().constructType(Message.class));
    }

    @Override
    public Message sendPaidMedia(SendPaidMediaRequest request) {
        SendPaidMediaRequest actualRequest = Objects.requireNonNull(request, "request must not be null");
        boolean hasUpload = actualRequest.media().stream().map(InputPaidMedia::media).anyMatch(InputFile::isUpload);

        if (!hasUpload) {
            SendPaidMediaJsonPayload payload = new SendPaidMediaJsonPayload(
                actualRequest.businessConnectionId(),
                actualRequest.chatId(),
                actualRequest.starCount(),
                actualRequest.media().stream()
                    .map(this::toPaidMediaPayloadWithReference)
                    .toList(),
                actualRequest.payload(),
                actualRequest.caption(),
                actualRequest.parseMode(),
                actualRequest.captionEntities(),
                actualRequest.showCaptionAboveMedia(),
                actualRequest.disableNotification()
            );
            return invoke("sendPaidMedia", payload, objectMapper.getTypeFactory().constructType(Message.class));
        }

        return sendPaidMediaMultipart(actualRequest);
    }

    @Override
    public boolean answerShippingQuery(AnswerShippingQueryRequest request) {
        Boolean result = invoke("answerShippingQuery", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean answerPreCheckoutQuery(AnswerPreCheckoutQueryRequest request) {
        Boolean result = invoke("answerPreCheckoutQuery", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public Story postStory(PostStoryRequest request) {
        return postOrEditStoryMultipart("postStory", Objects.requireNonNull(request, "request must not be null"), objectMapper.getTypeFactory().constructType(Story.class));
    }

    @Override
    public Story editStory(EditStoryRequest request) {
        return postOrEditStoryMultipart("editStory", Objects.requireNonNull(request, "request must not be null"), objectMapper.getTypeFactory().constructType(Story.class));
    }

    @Override
    public boolean deleteStory(DeleteStoryRequest request) {
        Boolean result = invoke("deleteStory", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public Story repostStory(RepostStoryRequest request) {
        return invoke("repostStory", requireRequest(request), objectMapper.getTypeFactory().constructType(Story.class));
    }

    @Override
    public Message sendChecklist(SendChecklistRequest request) {
        return invoke("sendChecklist", requireRequest(request), objectMapper.getTypeFactory().constructType(Message.class));
    }

    @Override
    public Message editMessageChecklist(EditMessageChecklistRequest request) {
        return invoke("editMessageChecklist", requireRequest(request), objectMapper.getTypeFactory().constructType(Message.class));
    }

    @Override
    public Gifts getAvailableGifts() {
        return invoke("getAvailableGifts", null, objectMapper.getTypeFactory().constructType(Gifts.class));
    }

    @Override
    public boolean sendGift(SendGiftRequest request) {
        Boolean result = invoke("sendGift", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean giftPremiumSubscription(GiftPremiumSubscriptionRequest request) {
        Boolean result = invoke("giftPremiumSubscription", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public OwnedGifts getUserGifts(GetUserGiftsRequest request) {
        return invoke("getUserGifts", requireRequest(request), objectMapper.getTypeFactory().constructType(OwnedGifts.class));
    }

    @Override
    public OwnedGifts getChatGifts(GetChatGiftsRequest request) {
        return invoke("getChatGifts", requireRequest(request), objectMapper.getTypeFactory().constructType(OwnedGifts.class));
    }

    @Override
    public ChatInviteLink createChatSubscriptionInviteLink(CreateChatSubscriptionInviteLinkRequest request) {
        return invoke(
            "createChatSubscriptionInviteLink",
            requireRequest(request),
            objectMapper.getTypeFactory().constructType(ChatInviteLink.class)
        );
    }

    @Override
    public ChatInviteLink editChatSubscriptionInviteLink(EditChatSubscriptionInviteLinkRequest request) {
        return invoke(
            "editChatSubscriptionInviteLink",
            requireRequest(request),
            objectMapper.getTypeFactory().constructType(ChatInviteLink.class)
        );
    }

    @Override
    public boolean setBusinessAccountGiftSettings(SetBusinessAccountGiftSettingsRequest request) {
        Boolean result = invoke("setBusinessAccountGiftSettings", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public StarAmount getBusinessAccountStarBalance(GetBusinessAccountStarBalanceRequest request) {
        return invoke("getBusinessAccountStarBalance", requireRequest(request), objectMapper.getTypeFactory().constructType(StarAmount.class));
    }

    @Override
    public boolean transferBusinessAccountStars(TransferBusinessAccountStarsRequest request) {
        Boolean result = invoke("transferBusinessAccountStars", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public OwnedGifts getBusinessAccountGifts(GetBusinessAccountGiftsRequest request) {
        return invoke("getBusinessAccountGifts", requireRequest(request), objectMapper.getTypeFactory().constructType(OwnedGifts.class));
    }

    @Override
    public boolean convertGiftToStars(ConvertGiftToStarsRequest request) {
        Boolean result = invoke("convertGiftToStars", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean upgradeGift(UpgradeGiftRequest request) {
        Boolean result = invoke("upgradeGift", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean transferGift(TransferGiftRequest request) {
        Boolean result = invoke("transferGift", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public StarAmount getMyStarBalance() {
        return invoke("getMyStarBalance", null, objectMapper.getTypeFactory().constructType(StarAmount.class));
    }

    @Override
    public StarTransactions getStarTransactions(GetStarTransactionsRequest request) {
        GetStarTransactionsRequest actualRequest = request == null ? new GetStarTransactionsRequest(null, null) : request;
        return invoke("getStarTransactions", actualRequest, objectMapper.getTypeFactory().constructType(StarTransactions.class));
    }

    @Override
    public boolean refundStarPayment(RefundStarPaymentRequest request) {
        Boolean result = invoke("refundStarPayment", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean editUserStarSubscription(EditUserStarSubscriptionRequest request) {
        Boolean result = invoke("editUserStarSubscription", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public BusinessConnection getBusinessConnection(GetBusinessConnectionRequest request) {
        return invoke("getBusinessConnection", requireRequest(request), objectMapper.getTypeFactory().constructType(BusinessConnection.class));
    }

    @Override
    public boolean readBusinessMessage(ReadBusinessMessageRequest request) {
        Boolean result = invoke("readBusinessMessage", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean deleteBusinessMessages(DeleteBusinessMessagesRequest request) {
        Boolean result = invoke("deleteBusinessMessages", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean setMyCommands(SetMyCommandsRequest request) {
        Boolean result = invoke("setMyCommands", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public List<BotCommand> getMyCommands(GetMyCommandsRequest request) {
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, BotCommand.class);
        GetMyCommandsRequest actualRequest = request == null ? new GetMyCommandsRequest(null, null) : request;
        return invoke("getMyCommands", actualRequest, listType);
    }

    @Override
    public boolean setChatMenuButton(SetChatMenuButtonRequest request) {
        SetChatMenuButtonRequest actualRequest = request == null ? new SetChatMenuButtonRequest(null, null) : request;
        Boolean result = invoke("setChatMenuButton", actualRequest, objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public MenuButton getChatMenuButton(GetChatMenuButtonRequest request) {
        GetChatMenuButtonRequest actualRequest = request == null ? new GetChatMenuButtonRequest(null) : request;
        return invoke("getChatMenuButton", actualRequest, objectMapper.getTypeFactory().constructType(MenuButton.class));
    }

    @Override
    public TelegramFile getFile(GetFileRequest request) {
        return invoke("getFile", requireRequest(request), objectMapper.getTypeFactory().constructType(TelegramFile.class));
    }

    @Override
    public Message sendDocument(SendDocumentRequest request) {
        SendDocumentRequest actualRequest = Objects.requireNonNull(request, "request must not be null");
        InputFile inputFile = actualRequest.document();

        if (inputFile instanceof InputFileReference reference) {
            SendDocumentJsonPayload jsonPayload = new SendDocumentJsonPayload(
                actualRequest.chatId(),
                actualRequest.businessConnectionId(),
                reference.value(),
                actualRequest.caption(),
                actualRequest.replyMarkup()
            );
            return invoke("sendDocument", jsonPayload, objectMapper.getTypeFactory().constructType(Message.class));
        }

        return sendDocumentMultipart(actualRequest, inputFile);
    }

    @Override
    public List<Message> sendMediaGroup(SendMediaGroupRequest request) {
        SendMediaGroupRequest actualRequest = Objects.requireNonNull(request, "request must not be null");

        boolean hasUpload = actualRequest.media().stream().map(InputMedia::media).anyMatch(InputFile::isUpload);
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Message.class);

        if (!hasUpload) {
            SendMediaGroupJsonPayload payload = new SendMediaGroupJsonPayload(
                actualRequest.chatId(),
                actualRequest.businessConnectionId(),
                actualRequest.media().stream()
                    .map(this::toMediaPayloadWithReference)
                    .toList()
            );
            return invoke("sendMediaGroup", payload, listType);
        }

        return sendMediaGroupMultipart(actualRequest, listType);
    }

    @Override
    public String buildFileDownloadUrl(String filePath) {
        String actualFilePath = requireText(filePath, "filePath");
        String normalizedPath = actualFilePath.startsWith("/") ? actualFilePath.substring(1) : actualFilePath;
        return baseUrl + "/file/bot" + botToken + "/" + normalizedPath;
    }

    @Override
    public byte[] downloadFile(String filePath) {
        String rawBody = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(buildFileDownloadUrl(filePath)))
                .GET()
                .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                rawBody = new String(response.body(), StandardCharsets.UTF_8);
                throw new TelegramApiException(null, "Failed to download file. HTTP status: " + response.statusCode(), rawBody);
            }
            return response.body();
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while downloading Telegram file", rawBody, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TelegramApiException(null, "Interrupted while downloading Telegram file", rawBody, e);
        }
    }

    @Override
    public Path downloadFile(String filePath, Path targetPath) {
        Objects.requireNonNull(targetPath, "targetPath must not be null");
        byte[] content = downloadFile(filePath);
        try {
            Path parent = targetPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            return Files.write(targetPath, content);
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while writing downloaded file to disk", null, e);
        }
    }

    @Override
    public boolean setWebhook(SetWebhookRequest request) {
        Boolean result = invoke("setWebhook", requireRequest(request), objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean deleteWebhook(DeleteWebhookRequest request) {
        DeleteWebhookRequest actualRequest = request == null ? new DeleteWebhookRequest(null) : request;
        Boolean result = invoke("deleteWebhook", actualRequest, objectMapper.getTypeFactory().constructType(Boolean.class));
        return Boolean.TRUE.equals(result);
    }

    @Override
    public WebhookInfo getWebhookInfo() {
        return invoke("getWebhookInfo", null, objectMapper.getTypeFactory().constructType(WebhookInfo.class));
    }

    private Object requireRequest(Object request) {
        return Objects.requireNonNull(request, "request must not be null");
    }

    private <T> T invoke(String methodName, Object requestBody, JavaType resultType) {
        String rawBody = null;
        try {
            HttpRequest request = buildRequest(methodName, requestBody);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            rawBody = response.body();
            TelegramApiResponse<T> envelope = parseApiResponse(rawBody, resultType, objectMapper);

            if (!Boolean.TRUE.equals(envelope.ok())) {
                throw new TelegramApiException(envelope.errorCode(), envelope.description(), rawBody);
            }
            return envelope.result();
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while calling Telegram Bot API", rawBody, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TelegramApiException(null, "Interrupted while calling Telegram Bot API", rawBody, e);
        }
    }

    private Message sendDocumentMultipart(SendDocumentRequest request, InputFile inputFile) {
        try {
            MultipartFormData multipart = new MultipartFormData()
                .addField("chat_id", String.valueOf(request.chatId()));
            if (request.businessConnectionId() != null) {
                multipart.addField("business_connection_id", request.businessConnectionId());
            }
            if (request.caption() != null) {
                multipart.addField("caption", request.caption());
            }
            if (request.replyMarkup() != null) {
                multipart.addField("reply_markup", objectMapper.writeValueAsString(request.replyMarkup()));
            }

            addInputFilePart(multipart, "document", inputFile, "document");
            MultipartFormData.BuiltMultipart builtMultipart = multipart.build();
            return invokeMultipart("sendDocument", builtMultipart, objectMapper.getTypeFactory().constructType(Message.class));
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart sendDocument request", null, e);
        }
    }

    private List<Message> sendMediaGroupMultipart(SendMediaGroupRequest request, JavaType listType) {
        try {
            MultipartFormData multipart = new MultipartFormData()
                .addField("chat_id", String.valueOf(request.chatId()));
            if (request.businessConnectionId() != null) {
                multipart.addField("business_connection_id", request.businessConnectionId());
            }

            AtomicInteger counter = new AtomicInteger(0);
            List<SendMediaGroupItemPayload> payloadItems = request.media().stream()
                .map(item -> toMediaPayload(item, multipart, counter))
                .toList();

            multipart.addField("media", objectMapper.writeValueAsString(payloadItems));
            MultipartFormData.BuiltMultipart builtMultipart = multipart.build();
            return invokeMultipart("sendMediaGroup", builtMultipart, listType);
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart sendMediaGroup request", null, e);
        }
    }

    private Message sendPaidMediaMultipart(SendPaidMediaRequest request) {
        try {
            MultipartFormData multipart = new MultipartFormData()
                .addField("chat_id", String.valueOf(request.chatId()))
                .addField("star_count", String.valueOf(request.starCount()));
            if (request.businessConnectionId() != null) {
                multipart.addField("business_connection_id", request.businessConnectionId());
            }
            if (request.payload() != null) {
                multipart.addField("payload", request.payload());
            }
            if (request.caption() != null) {
                multipart.addField("caption", request.caption());
            }
            if (request.parseMode() != null) {
                multipart.addField("parse_mode", request.parseMode());
            }
            if (request.captionEntities() != null) {
                multipart.addField("caption_entities", objectMapper.writeValueAsString(request.captionEntities()));
            }
            if (request.showCaptionAboveMedia() != null) {
                multipart.addField("show_caption_above_media", String.valueOf(request.showCaptionAboveMedia()));
            }
            if (request.disableNotification() != null) {
                multipart.addField("disable_notification", String.valueOf(request.disableNotification()));
            }

            AtomicInteger counter = new AtomicInteger(0);
            List<SendPaidMediaItemPayload> payloadItems = request.media().stream()
                .map(item -> toPaidMediaPayload(item, multipart, counter))
                .toList();

            multipart.addField("media", objectMapper.writeValueAsString(payloadItems));
            MultipartFormData.BuiltMultipart builtMultipart = multipart.build();
            return invokeMultipart("sendPaidMedia", builtMultipart, objectMapper.getTypeFactory().constructType(Message.class));
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart sendPaidMedia request", null, e);
        }
    }

    private Story postOrEditStoryMultipart(String methodName, PostStoryRequest request, JavaType resultType) {
        try {
            MultipartFormData multipart = new MultipartFormData()
                .addField("business_connection_id", request.businessConnectionId())
                .addField("active_period", String.valueOf(request.activePeriod()));
            if (request.caption() != null) {
                multipart.addField("caption", request.caption());
            }
            if (request.parseMode() != null) {
                multipart.addField("parse_mode", request.parseMode());
            }
            if (request.captionEntities() != null) {
                multipart.addField("caption_entities", objectMapper.writeValueAsString(request.captionEntities()));
            }
            if (request.areas() != null) {
                multipart.addField("areas", objectMapper.writeValueAsString(request.areas()));
            }
            if (request.postToChatPage() != null) {
                multipart.addField("post_to_chat_page", String.valueOf(request.postToChatPage()));
            }
            if (request.protectContent() != null) {
                multipart.addField("protect_content", String.valueOf(request.protectContent()));
            }
            multipart.addField("content", objectMapper.writeValueAsString(toStoryContentPayload(request.content(), multipart)));
            MultipartFormData.BuiltMultipart builtMultipart = multipart.build();
            return invokeMultipart(methodName, builtMultipart, resultType);
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart " + methodName + " request", null, e);
        }
    }

    private Story postOrEditStoryMultipart(String methodName, EditStoryRequest request, JavaType resultType) {
        try {
            MultipartFormData multipart = new MultipartFormData()
                .addField("business_connection_id", request.businessConnectionId())
                .addField("story_id", String.valueOf(request.storyId()));
            if (request.caption() != null) {
                multipart.addField("caption", request.caption());
            }
            if (request.parseMode() != null) {
                multipart.addField("parse_mode", request.parseMode());
            }
            if (request.captionEntities() != null) {
                multipart.addField("caption_entities", objectMapper.writeValueAsString(request.captionEntities()));
            }
            if (request.areas() != null) {
                multipart.addField("areas", objectMapper.writeValueAsString(request.areas()));
            }
            multipart.addField("content", objectMapper.writeValueAsString(toStoryContentPayload(request.content(), multipart)));
            MultipartFormData.BuiltMultipart builtMultipart = multipart.build();
            return invokeMultipart(methodName, builtMultipart, resultType);
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart " + methodName + " request", null, e);
        }
    }

    private StoryContentPayload toStoryContentPayload(InputStoryContent content, MultipartFormData multipart) {
        InputFile media = content.media();
        if (media instanceof InputFileReference reference) {
            return new StoryContentPayload(content.type(), reference.value(), null, null, null);
        }
        String attachName = "storymedia";
        try {
            addInputFilePart(multipart, attachName, media, attachName);
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart story media part", null, e);
        }
        if (content instanceof ru.tardyon.botframework.telegram.api.model.story.InputStoryContentVideo video) {
            return new StoryContentPayload(content.type(), "attach://" + attachName, video.duration(), video.coverFrameTimestamp(), video.isAnimation());
        }
        return new StoryContentPayload(content.type(), "attach://" + attachName, null, null, null);
    }

    private SendMediaGroupItemPayload toMediaPayloadWithReference(InputMedia inputMedia) {
        return new SendMediaGroupItemPayload(
            inputMedia.type(),
            inputMedia.media().asReference(),
            inputMedia.caption(),
            inputMedia.parseMode(),
            inputMedia.captionEntities()
        );
    }

    private SendMediaGroupItemPayload toMediaPayload(
        InputMedia inputMedia,
        MultipartFormData multipart,
        AtomicInteger counter
    ) {
        InputFile mediaFile = inputMedia.media();
        if (mediaFile instanceof InputFileReference reference) {
            return new SendMediaGroupItemPayload(
                inputMedia.type(),
                reference.value(),
                inputMedia.caption(),
                inputMedia.parseMode(),
                inputMedia.captionEntities()
            );
        }

        String attachName = "media" + counter.incrementAndGet();
        try {
            addInputFilePart(multipart, attachName, mediaFile, attachName);
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart media part", null, e);
        }

        return new SendMediaGroupItemPayload(
            inputMedia.type(),
            "attach://" + attachName,
            inputMedia.caption(),
            inputMedia.parseMode(),
            inputMedia.captionEntities()
        );
    }

    private SendPaidMediaItemPayload toPaidMediaPayloadWithReference(InputPaidMedia inputPaidMedia) {
        return new SendPaidMediaItemPayload(
            inputPaidMedia.type(),
            inputPaidMedia.media().asReference()
        );
    }

    private SendPaidMediaItemPayload toPaidMediaPayload(
        InputPaidMedia inputPaidMedia,
        MultipartFormData multipart,
        AtomicInteger counter
    ) {
        InputFile mediaFile = inputPaidMedia.media();
        if (mediaFile instanceof InputFileReference reference) {
            return new SendPaidMediaItemPayload(inputPaidMedia.type(), reference.value());
        }
        String attachName = "paidmedia" + counter.incrementAndGet();
        try {
            addInputFilePart(multipart, attachName, mediaFile, attachName);
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while preparing multipart paid media part", null, e);
        }
        return new SendPaidMediaItemPayload(inputPaidMedia.type(), "attach://" + attachName);
    }

    private void addInputFilePart(MultipartFormData multipart, String partName, InputFile inputFile, String defaultFilename) throws IOException {
        if (inputFile instanceof InputFilePath filePath) {
            byte[] content = Files.readAllBytes(filePath.path());
            multipart.addFile(partName, filePath.resolvedFilename(), filePath.contentType(), content);
            return;
        }

        if (inputFile instanceof InputFileBytes inputFileBytes) {
            multipart.addFile(partName, inputFileBytes.filename(), inputFileBytes.contentType(), inputFileBytes.content());
            return;
        }

        if (inputFile instanceof InputFileStream inputFileStream) {
            byte[] content = readAllBytes(inputFileStream.inputStream());
            multipart.addFile(partName, inputFileStream.filename(), inputFileStream.contentType(), content);
            return;
        }

        if (inputFile instanceof InputFileReference reference) {
            multipart.addField(partName, reference.value());
            return;
        }

        throw new IllegalArgumentException(
            "Unsupported input file type for upload in " + defaultFilename + ": " + inputFile.getClass().getName()
        );
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        try (InputStream in = inputStream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
    }

    private <T> T invokeMultipart(String methodName, MultipartFormData.BuiltMultipart multipart, JavaType resultType) {
        String rawBody = null;
        try {
            HttpRequest request = HttpRequest.newBuilder(buildMethodUri(methodName))
                .header("Accept", "application/json")
                .header("Content-Type", multipart.contentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipart.body()))
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            rawBody = response.body();
            TelegramApiResponse<T> envelope = parseApiResponse(rawBody, resultType, objectMapper);
            if (!Boolean.TRUE.equals(envelope.ok())) {
                throw new TelegramApiException(envelope.errorCode(), envelope.description(), rawBody);
            }
            return envelope.result();
        } catch (IOException e) {
            throw new TelegramApiException(null, "I/O error while calling Telegram Bot API", rawBody, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TelegramApiException(null, "Interrupted while calling Telegram Bot API", rawBody, e);
        }
    }

    private HttpRequest buildRequest(String methodName, Object requestBody) throws JsonProcessingException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(buildMethodUri(methodName))
            .header("Accept", "application/json");

        if (requestBody == null) {
            return builder.POST(HttpRequest.BodyPublishers.noBody()).build();
        }

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        return builder
            .header("Content-Type", APPLICATION_JSON)
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
            .build();
    }

    private URI buildMethodUri(String methodName) {
        return URI.create(baseUrl + "/bot" + botToken + "/" + methodName);
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private record SendDocumentJsonPayload(
        @JsonProperty("chat_id") Object chatId,
        @JsonProperty("business_connection_id") String businessConnectionId,
        String document,
        String caption,
        @JsonProperty("reply_markup") ReplyMarkup replyMarkup
    ) {
    }

    private record SendMediaGroupJsonPayload(
        @JsonProperty("chat_id") Object chatId,
        @JsonProperty("business_connection_id") String businessConnectionId,
        List<SendMediaGroupItemPayload> media
    ) {
    }

    private record SendMediaGroupItemPayload(
        String type,
        String media,
        String caption,
        @JsonProperty("parse_mode") String parseMode,
        @JsonProperty("caption_entities") List<MessageEntity> captionEntities
    ) {
    }

    private record SendPaidMediaJsonPayload(
        @JsonProperty("business_connection_id") String businessConnectionId,
        @JsonProperty("chat_id") Object chatId,
        @JsonProperty("star_count") Integer starCount,
        List<SendPaidMediaItemPayload> media,
        String payload,
        String caption,
        @JsonProperty("parse_mode") String parseMode,
        @JsonProperty("caption_entities") List<MessageEntity> captionEntities,
        @JsonProperty("show_caption_above_media") Boolean showCaptionAboveMedia,
        @JsonProperty("disable_notification") Boolean disableNotification
    ) {
    }

    private record SendPaidMediaItemPayload(
        String type,
        String media
    ) {
    }

    private record StoryContentPayload(
        String type,
        String media,
        Double duration,
        @JsonProperty("cover_frame_timestamp") Double coverFrameTimestamp,
        @JsonProperty("is_animation") Boolean isAnimation
    ) {
    }

    static <T> TelegramApiResponse<T> parseApiResponse(String rawBody, JavaType resultType, ObjectMapper objectMapper) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType responseType = typeFactory.constructParametricType(TelegramApiResponse.class, resultType);
            return objectMapper.readValue(rawBody, responseType);
        } catch (JsonProcessingException e) {
            throw new TelegramApiException(null, "Failed to parse Telegram Bot API response", rawBody, e);
        }
    }
}
