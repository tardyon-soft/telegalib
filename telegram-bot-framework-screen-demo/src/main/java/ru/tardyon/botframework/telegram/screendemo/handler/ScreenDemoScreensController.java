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
import ru.tardyon.botframework.telegram.spring.boot.widget.AnnotatedWidgetRegistry;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetView;

@ScreenController
public class ScreenDemoScreensController {

    private static final String HOME_SCREEN = "home";
    private static final String SETTINGS_SCREEN = "settings";
    private static final String PROFILE_SCREEN = "profile";
    private static final String CATALOG_LIST_SCREEN = "catalog_list";
    private static final String CATALOG_DETAILS_SCREEN = "catalog_details";

    private static final String TOGGLE_NOTIFICATIONS = "screen:screen:toggle_notifications:";
    private static final String HOME_MENU_WIDGET = "home_menu";
    private static final String OBJECTS_LIST_WIDGET = "objects_list";

    private static final List<MenuEntry> HOME_MENU = List.of(
        new MenuEntry("Настройки", SETTINGS_SCREEN),
        new MenuEntry("Профиль", PROFILE_SCREEN),
        new MenuEntry("Каталог", CATALOG_LIST_SCREEN)
    );

    private static final List<CatalogItem> CATALOG_ITEMS = List.of(
        new CatalogItem("item_1", "Объект 1", "Описание объекта 1"),
        new CatalogItem("item_2", "Объект 2", "Описание объекта 2"),
        new CatalogItem("item_3", "Объект 3", "Описание объекта 3")
    );

    private final AnnotatedWidgetRegistry widgetRegistry;

    public ScreenDemoScreensController(AnnotatedWidgetRegistry widgetRegistry) {
        this.widgetRegistry = widgetRegistry;
    }

    record MenuEntry(String label, String targetScreen) {
    }

    public record CatalogItem(String id, String title, String description) {
    }

    @Screen(id = HOME_SCREEN, startCommand = "screen_start")
    public ScreenView home(ScreenContext context) {
        ScreenView base = ScreenView.builder()
            .widgets(List.of(
                Widgets.line("Экран: HOME"),
                Widgets.line("Один активный экран на чат."),
                Widgets.line("Используйте кнопки ниже.")
            ), context)
            .build();

        WidgetView widgetView = widgetRegistry.render(HOME_MENU_WIDGET, context, HOME_MENU);
        widgetView.applyEffects(context);
        return widgetView.mergeInto(base);
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

    @Screen(id = CATALOG_LIST_SCREEN)
    public ScreenView catalogList(ScreenContext context) {
        ScreenView base = ScreenView.builder()
            .line("Экран: CATALOG")
            .line("Нажмите на объект для просмотра деталей")
            .build();
        WidgetView widgetView = widgetRegistry.render(OBJECTS_LIST_WIDGET, context, CATALOG_ITEMS);
        widgetView.applyEffects(context);
        return widgetView.mergeInto(base);
    }

    @Screen(id = CATALOG_DETAILS_SCREEN)
    public ScreenView catalogDetails(ScreenContext context) {
        String selectedId = (String) context.screenState().getData("selected_item_id").orElse(null);
        CatalogItem selected = CATALOG_ITEMS.stream().filter(item -> item.id().equals(selectedId)).findFirst().orElse(null);
        InlineKeyboardMarkup keyboard = Keyboards.inlineKeyboard()
            .row(Keyboards.callbackButton("Назад", ScreenCallbackData.back()))
            .build();
        if (selected == null) {
            return ScreenView.builder()
                .line("Объект не найден")
                .replyMarkup(keyboard)
                .build();
        }
        return ScreenView.builder()
            .line("Детали объекта")
            .line("ID: " + selected.id())
            .line("Название: " + selected.title())
            .line("Описание: " + selected.description())
            .replyMarkup(keyboard)
            .build();
    }

    @OnScreenMessage(screen = HOME_SCREEN, textEquals = "back")
    public ScreenAction backFromMessage(Message message) {
        return ScreenAction.back();
    }

    @OnScreenCallback(screen = HOME_SCREEN, callbackPrefix = "w:home_menu:")
    public ScreenAction onHomeWidgetCallback(ScreenContext context, CallbackQuery callbackQuery, TelegramCallbackQuery callback) {
        callback.answer();
        return widgetRegistry.handleCallback(callbackQuery.data(), context).orElse(ScreenAction.unhandled());
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

    @OnScreenCallback(screen = CATALOG_LIST_SCREEN, callbackPrefix = "w:objects_list:")
    public ScreenAction onCatalogWidgetCallback(ScreenContext context, CallbackQuery callbackQuery, TelegramCallbackQuery callback) {
        callback.answer();
        return widgetRegistry.handleCallback(callbackQuery.data(), context).orElse(ScreenAction.unhandled());
    }

    @OnScreenCallback(screen = CATALOG_DETAILS_SCREEN, callbackEquals = "screen:nav:back")
    public ScreenAction backFromCatalogDetails(TelegramCallbackQuery callback) {
        callback.answer();
        return ScreenAction.back();
    }
}
