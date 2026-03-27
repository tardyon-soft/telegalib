package ru.tardyon.botframework.telegram.dispatcher.filter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Chat;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.User;
import ru.tardyon.botframework.telegram.api.model.payment.PreCheckoutQuery;
import ru.tardyon.botframework.telegram.api.model.payment.ShippingQuery;
import ru.tardyon.botframework.telegram.dispatcher.command.CommandContext;
import ru.tardyon.botframework.telegram.dispatcher.command.CommandParser;
import ru.tardyon.botframework.telegram.fsm.State;

public final class Filters {

    private static final String CHAT_TYPE_PRIVATE = "private";
    private static final String CHAT_TYPE_GROUP = "group";
    private static final String CHAT_TYPE_SUPERGROUP = "supergroup";
    private static final String CHAT_TYPE_CHANNEL = "channel";

    private Filters() {
    }

    public static <E> Filter<E> any() {
        return event -> true;
    }

    public static Filter<Message> textPresent() {
        return message -> message != null && message.text() != null && !message.text().isBlank();
    }

    public static Filter<Message> textEquals(String expectedText) {
        Objects.requireNonNull(expectedText, "expectedText must not be null");
        return message -> message != null && expectedText.equals(message.text());
    }

    public static Filter<Message> textStartsWith(String prefix) {
        Objects.requireNonNull(prefix, "prefix must not be null");
        return message -> message != null && message.text() != null && message.text().startsWith(prefix);
    }

    public static Filter<Message> command(String command) {
        String normalized = CommandParser.normalizeExpectedCommand(command);
        return message -> commandContext(message)
            .map(context -> normalized.equals(context.command()))
            .orElse(false);
    }

    public static Filter<Message> commands(String... commands) {
        Objects.requireNonNull(commands, "commands must not be null");
        if (commands.length == 0) {
            throw new IllegalArgumentException("commands must not be empty");
        }

        Set<String> normalizedCommands = Arrays.stream(commands)
            .map(CommandParser::normalizeExpectedCommand)
            .collect(Collectors.toSet());

        return message -> commandContext(message)
            .map(context -> normalizedCommands.contains(context.command()))
            .orElse(false);
    }

    public static Optional<CommandContext> commandContext(Message message) {
        return CommandParser.parse(message);
    }

    public static Filter<Message> fromUser(long userId) {
        return message -> {
            if (message == null) {
                return false;
            }
            User from = message.from();
            return from != null && from.id() == userId;
        };
    }

    public static Filter<Message> fromChat(long chatId) {
        return message -> {
            if (message == null || message.chat() == null) {
                return false;
            }
            return message.chat().id() == chatId;
        };
    }

    public static Filter<Message> privateChat() {
        return message -> hasChatType(message, CHAT_TYPE_PRIVATE);
    }

    public static Filter<Message> groupChat() {
        return message -> hasChatType(message, CHAT_TYPE_GROUP);
    }

    public static Filter<Message> supergroupChat() {
        return message -> hasChatType(message, CHAT_TYPE_SUPERGROUP);
    }

    public static Filter<Message> channelChat() {
        return message -> hasChatType(message, CHAT_TYPE_CHANNEL);
    }

    public static Filter<CallbackQuery> callbackDataPresent() {
        return callbackQuery -> callbackQuery != null && callbackQuery.data() != null && !callbackQuery.data().isBlank();
    }

    public static Filter<CallbackQuery> callbackDataEquals(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return callbackQuery -> callbackQuery != null && value.equals(callbackQuery.data());
    }

    public static Filter<CallbackQuery> callbackDataStartsWith(String prefix) {
        Objects.requireNonNull(prefix, "prefix must not be null");
        return callbackQuery -> callbackQuery != null
            && callbackQuery.data() != null
            && callbackQuery.data().startsWith(prefix);
    }

    public static Filter<ShippingQuery> invoicePayloadEquals(String payload) {
        Objects.requireNonNull(payload, "payload must not be null");
        return shippingQuery -> shippingQuery != null && payload.equals(shippingQuery.invoicePayload());
    }

    public static Filter<PreCheckoutQuery> preCheckoutPayloadEquals(String payload) {
        Objects.requireNonNull(payload, "payload must not be null");
        return preCheckoutQuery -> preCheckoutQuery != null && payload.equals(preCheckoutQuery.invoicePayload());
    }

    public static <E> ContextFilter<E> stateEquals(String state) {
        return stateEquals(State.of(state));
    }

    public static <E> ContextFilter<E> stateEquals(State state) {
        Objects.requireNonNull(state, "state must not be null");
        return (updateContext, event) -> updateContext != null
            && updateContext.state().getState().map(state::equals).orElse(false);
    }

    public static <E> ContextFilter<E> inStates(String... states) {
        Objects.requireNonNull(states, "states must not be null");
        if (states.length == 0) {
            throw new IllegalArgumentException("states must not be empty");
        }
        Set<State> allowedStates = Arrays.stream(states).map(State::of).collect(Collectors.toSet());
        return inStates(allowedStates);
    }

    public static <E> ContextFilter<E> inStates(State... states) {
        Objects.requireNonNull(states, "states must not be null");
        if (states.length == 0) {
            throw new IllegalArgumentException("states must not be empty");
        }
        return inStates(Arrays.stream(states).collect(Collectors.toSet()));
    }

    public static <E> ContextFilter<E> noState() {
        return (updateContext, event) -> updateContext != null && updateContext.state().getState().isEmpty();
    }

    private static <E> ContextFilter<E> inStates(Set<State> states) {
        return (updateContext, event) -> updateContext != null
            && updateContext.state().getState().map(states::contains).orElse(false);
    }

    private static boolean hasChatType(Message message, String chatType) {
        if (message == null) {
            return false;
        }
        Chat chat = message.chat();
        return chat != null && chatType.equals(chat.type());
    }

}
