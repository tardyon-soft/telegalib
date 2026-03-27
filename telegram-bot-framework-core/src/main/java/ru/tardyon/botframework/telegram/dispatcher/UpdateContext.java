package ru.tardyon.botframework.telegram.dispatcher;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.ChosenInlineResult;
import ru.tardyon.botframework.telegram.api.model.InlineQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.Update;
import ru.tardyon.botframework.telegram.api.model.business.BusinessConnection;
import ru.tardyon.botframework.telegram.api.model.business.BusinessMessagesDeleted;
import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingQuery;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.command.CommandContext;
import ru.tardyon.botframework.telegram.dispatcher.command.CommandParser;
import ru.tardyon.botframework.telegram.fsm.InMemoryStateStorage;
import ru.tardyon.botframework.telegram.fsm.StateContext;
import ru.tardyon.botframework.telegram.fsm.StateKey;
import ru.tardyon.botframework.telegram.fsm.StateKeyResolver;
import ru.tardyon.botframework.telegram.fsm.StateStorage;

public final class UpdateContext {
    private static final StateStorage DEFAULT_STATE_STORAGE = new InMemoryStateStorage();

    public enum UpdateType {
        MESSAGE,
        EDITED_MESSAGE,
        CHANNEL_POST,
        EDITED_CHANNEL_POST,
        CALLBACK_QUERY,
        SHIPPING_QUERY,
        PRE_CHECKOUT_QUERY,
        BUSINESS_CONNECTION,
        BUSINESS_MESSAGE,
        EDITED_BUSINESS_MESSAGE,
        DELETED_BUSINESS_MESSAGES,
        INLINE_QUERY,
        CHOSEN_INLINE_RESULT,
        UNSUPPORTED
    }

    private final Update update;
    private final UpdateType updateType;
    private final Message message;
    private final CallbackQuery callbackQuery;
    private final ShippingQuery shippingQuery;
    private final PreCheckoutQuery preCheckoutQuery;
    private final BusinessConnection businessConnection;
    private final Message businessMessage;
    private final Message editedBusinessMessage;
    private final BusinessMessagesDeleted deletedBusinessMessages;
    private final InlineQuery inlineQuery;
    private final ChosenInlineResult chosenInlineResult;
    private final TelegramApiClient telegramApiClient;
    private final StateContext stateContext;
    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    public UpdateContext(Update update) {
        this(update, null, DEFAULT_STATE_STORAGE, "default-bot");
    }

    public UpdateContext(Update update, TelegramApiClient telegramApiClient) {
        this(update, telegramApiClient, DEFAULT_STATE_STORAGE, botIdFromClient(telegramApiClient));
    }

    public UpdateContext(
        Update update,
        TelegramApiClient telegramApiClient,
        StateStorage stateStorage,
        String botId
    ) {
        this.update = Objects.requireNonNull(update, "update must not be null");
        this.telegramApiClient = telegramApiClient;
        StateStorage actualStateStorage = Objects.requireNonNull(stateStorage, "stateStorage must not be null");
        String actualBotId = requireBotId(botId);
        StateKey stateKey = StateKeyResolver.resolve(update, actualBotId).orElse(null);
        this.stateContext = new StateContext(actualStateStorage, stateKey);

        if (update.message() != null) {
            this.updateType = UpdateType.MESSAGE;
            this.message = update.message();
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.editedMessage() != null) {
            this.updateType = UpdateType.EDITED_MESSAGE;
            this.message = update.editedMessage();
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.channelPost() != null) {
            this.updateType = UpdateType.CHANNEL_POST;
            this.message = update.channelPost();
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.editedChannelPost() != null) {
            this.updateType = UpdateType.EDITED_CHANNEL_POST;
            this.message = update.editedChannelPost();
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.callbackQuery() != null) {
            this.updateType = UpdateType.CALLBACK_QUERY;
            this.message = null;
            this.callbackQuery = update.callbackQuery();
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.shippingQuery() != null) {
            this.updateType = UpdateType.SHIPPING_QUERY;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = update.shippingQuery();
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.preCheckoutQuery() != null) {
            this.updateType = UpdateType.PRE_CHECKOUT_QUERY;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = update.preCheckoutQuery();
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.businessConnection() != null) {
            this.updateType = UpdateType.BUSINESS_CONNECTION;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = update.businessConnection();
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.businessMessage() != null) {
            this.updateType = UpdateType.BUSINESS_MESSAGE;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = update.businessMessage();
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.editedBusinessMessage() != null) {
            this.updateType = UpdateType.EDITED_BUSINESS_MESSAGE;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = update.editedBusinessMessage();
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.deletedBusinessMessages() != null) {
            this.updateType = UpdateType.DELETED_BUSINESS_MESSAGES;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = update.deletedBusinessMessages();
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        } else if (update.inlineQuery() != null) {
            this.updateType = UpdateType.INLINE_QUERY;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = update.inlineQuery();
            this.chosenInlineResult = null;
        } else if (update.chosenInlineResult() != null) {
            this.updateType = UpdateType.CHOSEN_INLINE_RESULT;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = update.chosenInlineResult();
        } else {
            this.updateType = UpdateType.UNSUPPORTED;
            this.message = null;
            this.callbackQuery = null;
            this.shippingQuery = null;
            this.preCheckoutQuery = null;
            this.businessConnection = null;
            this.businessMessage = null;
            this.editedBusinessMessage = null;
            this.deletedBusinessMessages = null;
            this.inlineQuery = null;
            this.chosenInlineResult = null;
        }
    }

    public Update getUpdate() {
        return update;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public Message getMessage() {
        return message;
    }

    public CallbackQuery getCallbackQuery() {
        return callbackQuery;
    }

    public ShippingQuery getShippingQuery() {
        return shippingQuery;
    }

    public PreCheckoutQuery getPreCheckoutQuery() {
        return preCheckoutQuery;
    }

    public BusinessConnection getBusinessConnection() {
        return businessConnection;
    }

    public Message getBusinessMessage() {
        return businessMessage;
    }

    public Message getEditedBusinessMessage() {
        return editedBusinessMessage;
    }

    public BusinessMessagesDeleted getDeletedBusinessMessages() {
        return deletedBusinessMessages;
    }

    public InlineQuery getInlineQuery() {
        return inlineQuery;
    }

    public ChosenInlineResult getChosenInlineResult() {
        return chosenInlineResult;
    }

    public TelegramMessage telegramMessage() {
        if (message == null) {
            return null;
        }
        return new TelegramMessage(message, telegramApiClient);
    }

    public TelegramCallbackQuery telegramCallbackQuery() {
        if (callbackQuery == null) {
            return null;
        }
        return new TelegramCallbackQuery(callbackQuery, telegramApiClient);
    }

    public void setAttribute(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        if (value == null) {
            attributes.remove(key);
            return;
        }
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        Objects.requireNonNull(key, "key must not be null");
        return (T) attributes.get(key);
    }

    public <T> Optional<T> findAttribute(String key) {
        return Optional.ofNullable(getAttribute(key));
    }

    public Optional<CommandContext> commandContext() {
        return CommandParser.parse(message);
    }

    public StateContext state() {
        return stateContext;
    }

    private static String requireBotId(String botId) {
        Objects.requireNonNull(botId, "botId must not be null");
        if (botId.isBlank()) {
            throw new IllegalArgumentException("botId must not be blank");
        }
        return botId;
    }

    private static String botIdFromClient(TelegramApiClient telegramApiClient) {
        if (telegramApiClient == null) {
            return "default-bot";
        }
        return telegramApiClient.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(telegramApiClient));
    }
}
