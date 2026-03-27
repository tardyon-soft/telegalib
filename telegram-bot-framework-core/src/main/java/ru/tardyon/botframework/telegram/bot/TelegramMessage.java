package ru.tardyon.botframework.telegram.bot;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.DeleteMessageRequest;
import ru.tardyon.botframework.telegram.api.method.DeleteBusinessMessagesRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageTextRequest;
import ru.tardyon.botframework.telegram.api.method.ReadBusinessMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.EditMessageReplyMarkupResult;
import ru.tardyon.botframework.telegram.api.model.EditMessageTextResult;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;

public final class TelegramMessage {

    private final Message message;
    private final TelegramApiClient telegramApiClient;

    public TelegramMessage(Message message, TelegramApiClient telegramApiClient) {
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.telegramApiClient = telegramApiClient;
    }

    public Message raw() {
        return message;
    }

    public Message reply(String text) {
        return reply(new SendMessageRequest(requireChatId(), text, null));
    }

    public Message reply(String text, ReplyMarkup replyMarkup) {
        return reply(new SendMessageRequest(requireChatId(), text, replyMarkup));
    }

    public Message reply(SendMessageRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return requireApiClient().sendMessage(new SendMessageRequest(requireChatId(), request.text(), request.replyMarkup(), request.businessConnectionId()));
    }

    public EditMessageTextResult editText(String text) {
        String businessConnectionId = requireBusinessConnectionIdOrNull();
        if (businessConnectionId == null) {
            return requireApiClient().editMessageText(EditMessageTextRequest.forChatMessage(requireChatId(), requireMessageId(), text));
        }
        return requireApiClient().editMessageText(
            EditMessageTextRequest.forBusinessChatMessage(businessConnectionId, requireChatId(), requireMessageId(), text)
        );
    }

    public boolean delete() {
        return requireApiClient().deleteMessage(DeleteMessageRequest.of(requireChatId(), requireMessageId()));
    }

    public EditMessageReplyMarkupResult editReplyMarkup(InlineKeyboardMarkup replyMarkup) {
        String businessConnectionId = requireBusinessConnectionIdOrNull();
        if (businessConnectionId == null) {
            return requireApiClient().editMessageReplyMarkup(
                EditMessageReplyMarkupRequest.forChatMessage(requireChatId(), requireMessageId(), replyMarkup)
            );
        }
        return requireApiClient().editMessageReplyMarkup(
            EditMessageReplyMarkupRequest.forBusinessChatMessage(
                businessConnectionId,
                requireChatId(),
                requireMessageId(),
                replyMarkup
            )
        );
    }

    public boolean readAsBusiness() {
        return requireApiClient().readBusinessMessage(
            new ReadBusinessMessageRequest(requireBusinessConnectionId(), requireChatId(), requireMessageId())
        );
    }

    public boolean deleteAsBusiness() {
        return requireApiClient().deleteBusinessMessages(
            new DeleteBusinessMessagesRequest(requireBusinessConnectionId(), java.util.List.of(requireMessageId()))
        );
    }

    private TelegramApiClient requireApiClient() {
        if (telegramApiClient == null) {
            throw new IllegalStateException("TelegramApiClient is not available in this context");
        }
        return telegramApiClient;
    }

    private long requireChatId() {
        if (message.chat() == null) {
            throw new IllegalStateException("Message.chat is required for this operation");
        }
        return message.chat().id();
    }

    private int requireMessageId() {
        if (message.messageId() == null) {
            throw new IllegalStateException("Message.messageId is required for this operation");
        }
        return message.messageId();
    }

    private String requireBusinessConnectionId() {
        if (message.businessConnectionId() == null || message.businessConnectionId().isBlank()) {
            throw new IllegalStateException("Message.businessConnectionId is required for business operation");
        }
        return message.businessConnectionId();
    }

    private String requireBusinessConnectionIdOrNull() {
        if (message.businessConnectionId() == null || message.businessConnectionId().isBlank()) {
            return null;
        }
        return message.businessConnectionId();
    }
}
