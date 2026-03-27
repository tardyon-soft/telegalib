package ru.tardyon.botframework.telegram.api.model.markup;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;
import ru.tardyon.botframework.telegram.api.model.webapp.WebAppInfo;

class KeyboardsSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesInlineKeyboardIntoReplyMarkup() throws Exception {
        InlineKeyboardMarkup markup = Keyboards.inlineKeyboard()
            .row(Keyboards.callbackButton("Menu", "menu:main"))
            .build();

        SendMessageRequest request = SendMessageRequest.of(123L, "Choose", markup);
        String json = objectMapper.writeValueAsString(request);

        assertTrue(json.contains("\"reply_markup\""));
        assertTrue(json.contains("\"inline_keyboard\""));
        assertTrue(json.contains("\"callback_data\":\"menu:main\""));
    }

    @Test
    void serializesReplyKeyboardIntoReplyMarkup() throws Exception {
        ReplyKeyboardMarkup markup = Keyboards.replyKeyboard()
            .row("Ping", "Help")
            .build();

        SendMessageRequest request = SendMessageRequest.of(123L, "Menu", markup);
        String json = objectMapper.writeValueAsString(request);

        assertTrue(json.contains("\"reply_markup\""));
        assertTrue(json.contains("\"keyboard\""));
        assertTrue(json.contains("\"Ping\""));
    }

    @Test
    void serializesAdvancedInlineButtons() throws Exception {
        InlineKeyboardMarkup markup = Keyboards.inlineKeyboard()
            .row(
                Keyboards.switchInlineQueryCurrentChatButton("Search here", ""),
                Keyboards.switchInlineQueryChosenChatButton(
                    "Choose chat",
                    new SwitchInlineQueryChosenChat("q", true, false, true, false)
                )
            )
            .row(Keyboards.copyTextButton("Copy", "copied text"))
            .build();

        SendMessageRequest request = SendMessageRequest.of(123L, "Buttons", markup);
        String json = objectMapper.writeValueAsString(request);

        assertTrue(json.contains("\"switch_inline_query_current_chat\":\"\""));
        assertTrue(json.contains("\"switch_inline_query_chosen_chat\""));
        assertTrue(json.contains("\"allow_user_chats\":true"));
        assertTrue(json.contains("\"copy_text\":{\"text\":\"copied text\"}"));
    }

    @Test
    void serializesWebAppButtons() throws Exception {
        InlineKeyboardMarkup inlineMarkup = Keyboards.inlineKeyboard()
            .row(Keyboards.webAppButton("Open App", new WebAppInfo("https://example.com/app")))
            .build();
        ReplyKeyboardMarkup replyMarkup = Keyboards.replyKeyboard()
            .rowButtons(Keyboards.replyWebAppButton("Launch", new WebAppInfo("https://example.com/miniapp")))
            .build();

        String inlineJson = objectMapper.writeValueAsString(SendMessageRequest.of(123L, "Inline app", inlineMarkup));
        String replyJson = objectMapper.writeValueAsString(SendMessageRequest.of(123L, "Reply app", replyMarkup));

        assertTrue(inlineJson.contains("\"web_app\":{\"url\":\"https://example.com/app\"}"));
        assertTrue(replyJson.contains("\"web_app\":{\"url\":\"https://example.com/miniapp\"}"));
    }
}
