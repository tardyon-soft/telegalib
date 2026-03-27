package ru.tardyon.botframework.telegram.api.model.markup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

public final class Keyboards {

    private Keyboards() {
    }

    public static InlineKeyboardBuilder inlineKeyboard() {
        return new InlineKeyboardBuilder();
    }

    public static ReplyKeyboardBuilder replyKeyboard() {
        return new ReplyKeyboardBuilder();
    }

    public static InlineKeyboardButton callbackButton(String text, String callbackData) {
        return InlineKeyboardButton.callback(text, callbackData);
    }

    public static InlineKeyboardButton urlButton(String text, String url) {
        return InlineKeyboardButton.url(text, url);
    }

    public static InlineKeyboardButton webAppButton(String text, WebAppInfo webApp) {
        return InlineKeyboardButton.webApp(text, webApp);
    }

    public static KeyboardButton replyWebAppButton(String text, WebAppInfo webApp) {
        return KeyboardButton.webApp(text, webApp);
    }

    public static InlineKeyboardButton switchInlineQueryButton(String text, String query) {
        return InlineKeyboardButton.switchInlineQuery(text, query);
    }

    public static InlineKeyboardButton switchInlineQueryCurrentChatButton(String text, String query) {
        return InlineKeyboardButton.switchInlineQueryCurrentChat(text, query);
    }

    public static InlineKeyboardButton switchInlineQueryChosenChatButton(
        String text,
        SwitchInlineQueryChosenChat switchInlineQueryChosenChat
    ) {
        return InlineKeyboardButton.switchInlineQueryChosenChat(text, switchInlineQueryChosenChat);
    }

    public static InlineKeyboardButton copyTextButton(String text, String copiedText) {
        return InlineKeyboardButton.copyText(text, new CopyTextButton(copiedText));
    }

    public static final class InlineKeyboardBuilder {

        private final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        public InlineKeyboardBuilder row(InlineKeyboardButton... buttons) {
            rows.add(List.copyOf(Arrays.asList(buttons)));
            return this;
        }

        public InlineKeyboardMarkup build() {
            return new InlineKeyboardMarkup(rows);
        }
    }

    public static final class ReplyKeyboardBuilder {

        private final List<List<KeyboardButton>> rows = new ArrayList<>();

        public ReplyKeyboardBuilder row(String... buttonTexts) {
            List<KeyboardButton> row = Arrays.stream(buttonTexts)
                .map(KeyboardButton::text)
                .toList();
            rows.add(row);
            return this;
        }

        public ReplyKeyboardBuilder rowButtons(KeyboardButton... buttons) {
            rows.add(List.copyOf(Arrays.asList(buttons)));
            return this;
        }

        public ReplyKeyboardMarkup build() {
            return new ReplyKeyboardMarkup(rows);
        }
    }
}
