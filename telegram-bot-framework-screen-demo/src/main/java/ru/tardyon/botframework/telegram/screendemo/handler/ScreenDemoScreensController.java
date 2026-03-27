package ru.tardyon.botframework.telegram.screendemo.handler;

import java.util.List;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenCallbackData;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.screen.ScreenView;
import ru.tardyon.botframework.telegram.screen.Widgets;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnScreenCallback;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnScreenMessage;
import ru.tardyon.botframework.telegram.spring.boot.annotation.Screen;
import ru.tardyon.botframework.telegram.spring.boot.annotation.ScreenController;

@ScreenController
public class ScreenDemoScreensController {

    private static final String HOME_SCREEN = "home";
    private static final String SETTINGS_SCREEN = "settings";
    private static final String PROFILE_SCREEN = "profile";

    private static final String SETTINGS_CALLBACK = "screen:screen:open:settings";
    private static final String PROFILE_CALLBACK = "screen:screen:open:profile";
    private static final String TOGGLE_NOTIFICATIONS = "screen:screen:toggle_notifications:";

    @Screen(id = HOME_SCREEN, startCommand = "screen_start")
    public ScreenView home(ScreenContext context) {
        InlineKeyboardMarkup keyboard = Keyboards.inlineKeyboard()
            .row(
                Keyboards.callbackButton("Настройки", SETTINGS_CALLBACK),
                Keyboards.callbackButton("Профиль", PROFILE_CALLBACK)
            )
            .build();

        return ScreenView.builder()
            .widgets(List.of(
                Widgets.line("Экран: HOME"),
                Widgets.line("Один активный экран на чат."),
                Widgets.line("Используйте кнопки ниже.")
            ), context)
            .replyMarkup(keyboard)
            .build();
    }

    @Screen(id = SETTINGS_SCREEN)
    public ScreenView settings(ScreenContext context) {
        boolean enabled = context.screenState().getData("notifications")
            .map(Boolean.class::cast)
            .orElse(false);
        InlineKeyboardMarkup keyboard = Keyboards.inlineKeyboard()
            .row(Keyboards.callbackButton(enabled ? "Отключить уведомления" : "Включить уведомления", TOGGLE_NOTIFICATIONS))
            .row(Keyboards.callbackButton("Назад", ScreenCallbackData.back()))
            .build();
        return ScreenView.builder()
            .line("Экран: SETTINGS")
            .line("notifications=" + enabled)
            .replyMarkup(keyboard)
            .build();
    }

    @Screen(id = PROFILE_SCREEN)
    public ScreenView profile(ScreenContext context) {
        Object userTheme = context.userState().getData("preferred_theme").orElse("<empty>");
        InlineKeyboardMarkup keyboard = Keyboards.inlineKeyboard()
            .row(Keyboards.callbackButton("Назад", ScreenCallbackData.back()))
            .build();
        return ScreenView.builder()
            .line("Экран: PROFILE")
            .line("Здесь читаем user state отдельно от screen state.")
            .line("preferred_theme=" + userTheme)
            .replyMarkup(keyboard)
            .build();
    }

    @OnScreenMessage(screen = HOME_SCREEN, textEquals = "back")
    public ScreenAction backFromMessage(Message message) {
        return ScreenAction.back();
    }

    @OnScreenCallback(screen = HOME_SCREEN, callbackEquals = SETTINGS_CALLBACK)
    public ScreenAction openSettings(TelegramCallbackQuery callback) {
        callback.answer();
        return ScreenAction.push(SETTINGS_SCREEN);
    }

    @OnScreenCallback(screen = HOME_SCREEN, callbackEquals = PROFILE_CALLBACK)
    public ScreenAction openProfile(TelegramCallbackQuery callback) {
        callback.answer();
        return ScreenAction.push(PROFILE_SCREEN);
    }

    @OnScreenCallback(screen = SETTINGS_SCREEN, callbackEquals = TOGGLE_NOTIFICATIONS)
    public ScreenAction toggleNotifications(ScreenContext context, TelegramCallbackQuery callback) {
        callback.answer();
        boolean enabled = context.screenState().getData("notifications")
            .map(Boolean.class::cast)
            .orElse(false);
        context.screenState().putData("notifications", !enabled);
        return ScreenAction.render();
    }

    @OnScreenCallback(screen = SETTINGS_SCREEN, callbackEquals = "screen:nav:back")
    public ScreenAction backFromSettings(TelegramCallbackQuery callback, CallbackQuery raw) {
        callback.answer();
        if (ScreenCallbackData.back().equals(raw.data())) {
            return ScreenAction.back();
        }
        return ScreenAction.unhandled();
    }

    @OnScreenCallback(screen = PROFILE_SCREEN, callbackEquals = "screen:nav:back")
    public ScreenAction backFromProfile(TelegramCallbackQuery callback) {
        callback.answer();
        return ScreenAction.back();
    }
}
