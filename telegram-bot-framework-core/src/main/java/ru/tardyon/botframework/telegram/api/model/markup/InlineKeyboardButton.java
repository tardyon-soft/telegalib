package ru.tardyon.botframework.telegram.api.model.markup;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

public record InlineKeyboardButton(
    String text,
    String url,
    @JsonProperty("callback_data") String callbackData,
    @JsonProperty("web_app") WebAppInfo webApp,
    @JsonProperty("switch_inline_query") String switchInlineQuery,
    @JsonProperty("switch_inline_query_current_chat") String switchInlineQueryCurrentChat,
    @JsonProperty("switch_inline_query_chosen_chat") SwitchInlineQueryChosenChat switchInlineQueryChosenChat,
    @JsonProperty("copy_text") CopyTextButton copyText
) {
    public InlineKeyboardButton {
        Objects.requireNonNull(text, "text must not be null");
        boolean hasUrl = url != null && !url.isBlank();
        boolean hasCallbackData = callbackData != null && !callbackData.isBlank();
        boolean hasWebApp = webApp != null;
        boolean hasSwitchInlineQuery = switchInlineQuery != null;
        boolean hasSwitchInlineQueryCurrentChat = switchInlineQueryCurrentChat != null;
        boolean hasSwitchInlineQueryChosenChat = switchInlineQueryChosenChat != null;
        boolean hasCopyText = copyText != null;

        int actionCount = 0;
        if (hasUrl) {
            actionCount++;
        }
        if (hasCallbackData) {
            actionCount++;
        }
        if (hasWebApp) {
            actionCount++;
        }
        if (hasSwitchInlineQuery) {
            actionCount++;
        }
        if (hasSwitchInlineQueryCurrentChat) {
            actionCount++;
        }
        if (hasSwitchInlineQueryChosenChat) {
            actionCount++;
        }
        if (hasCopyText) {
            actionCount++;
        }

        if (actionCount != 1) {
            throw new IllegalArgumentException(
                "Exactly one of url, callbackData, webApp, switchInlineQuery, switchInlineQueryCurrentChat, "
                    + "switchInlineQueryChosenChat or copyText must be provided"
            );
        }
    }

    public static InlineKeyboardButton callback(String text, String callbackData) {
        return new InlineKeyboardButton(text, null, callbackData, null, null, null, null, null);
    }

    public static InlineKeyboardButton url(String text, String url) {
        return new InlineKeyboardButton(text, url, null, null, null, null, null, null);
    }

    public static InlineKeyboardButton webApp(String text, WebAppInfo webApp) {
        Objects.requireNonNull(webApp, "webApp must not be null");
        return new InlineKeyboardButton(text, null, null, webApp, null, null, null, null);
    }

    public static InlineKeyboardButton switchInlineQuery(String text, String query) {
        return new InlineKeyboardButton(text, null, null, null, query, null, null, null);
    }

    public static InlineKeyboardButton switchInlineQueryCurrentChat(String text, String query) {
        return new InlineKeyboardButton(text, null, null, null, null, query, null, null);
    }

    public static InlineKeyboardButton switchInlineQueryChosenChat(
        String text,
        SwitchInlineQueryChosenChat switchInlineQueryChosenChat
    ) {
        return new InlineKeyboardButton(text, null, null, null, null, null, switchInlineQueryChosenChat, null);
    }

    public static InlineKeyboardButton copyText(String text, CopyTextButton copyText) {
        return new InlineKeyboardButton(text, null, null, null, null, null, null, copyText);
    }
}
