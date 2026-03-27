package ru.tardyon.botframework.telegram.bot;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.AnswerCallbackQueryRequest;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Message;

public final class TelegramCallbackQuery {

    private final CallbackQuery callbackQuery;
    private final TelegramApiClient telegramApiClient;

    public TelegramCallbackQuery(CallbackQuery callbackQuery, TelegramApiClient telegramApiClient) {
        this.callbackQuery = Objects.requireNonNull(callbackQuery, "callbackQuery must not be null");
        this.telegramApiClient = telegramApiClient;
    }

    public CallbackQuery raw() {
        return callbackQuery;
    }

    public boolean answer() {
        return requireApiClient().answerCallbackQuery(AnswerCallbackQueryRequest.of(requireCallbackQueryId()));
    }

    public boolean answer(String text) {
        return requireApiClient().answerCallbackQuery(
            new AnswerCallbackQueryRequest(requireCallbackQueryId(), text, null, null, null)
        );
    }

    public TelegramMessage message() {
        if (!(callbackQuery.message() instanceof Message message)) {
            return null;
        }
        return new TelegramMessage(message, telegramApiClient);
    }

    private TelegramApiClient requireApiClient() {
        if (telegramApiClient == null) {
            throw new IllegalStateException("TelegramApiClient is not available in this context");
        }
        return telegramApiClient;
    }

    private String requireCallbackQueryId() {
        if (callbackQuery.id() == null || callbackQuery.id().isBlank()) {
            throw new IllegalStateException("CallbackQuery.id is required for this operation");
        }
        return callbackQuery.id();
    }
}
