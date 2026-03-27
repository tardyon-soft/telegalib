package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.EditMessageReplyMarkupRequest;
import ru.tardyon.botframework.telegram.api.method.EditMessageTextRequest;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.method.SendPhotoRequest;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.ReplyMarkup;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;

public final class DefaultTelegramScreenViewRenderer implements ScreenViewRenderer {

    @Override
    public void render(UpdateContext updateContext, ScreenStateContext screenStateContext, long chatId, ScreenView view) {
        Objects.requireNonNull(updateContext, "updateContext must not be null");
        Objects.requireNonNull(screenStateContext, "screenStateContext must not be null");
        Objects.requireNonNull(view, "view must not be null");

        TelegramApiClient apiClient = updateContext.telegramApiClient();
        if (apiClient == null) {
            throw new IllegalStateException("TelegramApiClient is not available in UpdateContext");
        }

        if (view.photo() != null) {
            Message sentPhoto = apiClient.sendPhoto(new SendPhotoRequest(
                chatId,
                view.photo(),
                normalizeCaption(view.text()),
                view.replyMarkup()
            ));
            if (sentPhoto != null && sentPhoto.messageId() != null) {
                screenStateContext.setRenderedMessageId(sentPhoto.messageId());
                screenStateContext.setRenderedMessageKind(ScreenStack.RenderedMessageKind.PHOTO);
            }
            return;
        }

        Integer renderedMessageId = screenStateContext.renderedMessageId().orElse(null);
        ScreenStack.RenderedMessageKind renderedMessageKind = screenStateContext.renderedMessageKind().orElse(null);
        if (canEditExisting(view, renderedMessageId, renderedMessageKind)) {
            editExisting(apiClient, chatId, renderedMessageId, view);
            return;
        }

        Message sent = apiClient.sendMessage(new SendMessageRequest(chatId, view.text(), view.replyMarkup(), null));
        if (sent != null && sent.messageId() != null) {
            screenStateContext.setRenderedMessageId(sent.messageId());
            screenStateContext.setRenderedMessageKind(ScreenStack.RenderedMessageKind.TEXT);
        }
    }

    private boolean canEditExisting(
        ScreenView view,
        Integer renderedMessageId,
        ScreenStack.RenderedMessageKind renderedMessageKind
    ) {
        if (renderedMessageId == null) {
            return false;
        }
        if (renderedMessageKind == ScreenStack.RenderedMessageKind.PHOTO) {
            return false;
        }
        if (view.renderMode() == ScreenRenderMode.SEND_NEW) {
            return false;
        }
        if (view.renderMode() == ScreenRenderMode.EDIT_EXISTING) {
            return view.replyMarkup() == null || view.replyMarkup() instanceof InlineKeyboardMarkup;
        }
        return view.replyMarkup() == null || view.replyMarkup() instanceof InlineKeyboardMarkup;
    }

    private void editExisting(TelegramApiClient apiClient, long chatId, int messageId, ScreenView view) {
        apiClient.editMessageText(EditMessageTextRequest.forChatMessage(chatId, messageId, view.text()));
        InlineKeyboardMarkup inlineKeyboardMarkup = extractInlineMarkup(view.replyMarkup());
        apiClient.editMessageReplyMarkup(EditMessageReplyMarkupRequest.forChatMessage(chatId, messageId, inlineKeyboardMarkup));
    }

    private InlineKeyboardMarkup extractInlineMarkup(ReplyMarkup replyMarkup) {
        if (replyMarkup == null) {
            return null;
        }
        if (replyMarkup instanceof InlineKeyboardMarkup inlineKeyboardMarkup) {
            return inlineKeyboardMarkup;
        }
        return null;
    }

    private String normalizeCaption(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text;
    }
}
