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
    private static final String CHANNELS_LIST_WIDGET = "channels_list";

    private static final List<MenuEntry> HOME_MENU = List.of(
        new MenuEntry("Настройки", SETTINGS_SCREEN),
        new MenuEntry("Профиль", PROFILE_SCREEN),
        new MenuEntry("Каналы", CATALOG_LIST_SCREEN)
    );

    private static final List<ChannelItem> CHANNELS = List.of(
        new ChannelItem(
            "ch_1",
            "Tech Pulse",
            "Канал про Java, Telegram API и backend архитектуру.",
            128_000,
            52_000,
            "https://images.unsplash.com/photo-1755605983542-a525a0975a25?crop=entropy&cs=srgb&fm=jpg&ixid=M3w2MDcyNjN8MHwxfHJhbmRvbXx8fHx8fHx8fDE3NTgxOTM5Mzl8&ixlib=rb-4.1.0&q=85"
        ),
        new ChannelItem(
            "ch_2",
            "Design Feed",
            "Канал про интерфейсы, UX и визуальные паттерны.",
            97_500,
            38_400,
            "https://images.unsplash.com/photo-1755354113133-303805b81806?crop=entropy&cs=srgb&fm=jpg&ixid=M3w2MDcyNjN8MHwxfHJhbmRvbXx8fHx8fHx8fDE3NTgxOTQwNTh8&ixlib=rb-4.1.0&q=85"
        ),
        new ChannelItem(
            "ch_3",
            "Startup Radar",
            "Новости продуктов, growth и аналитика стартапов.",
            156_300,
            63_900,
            "https://images.unsplash.com/photo-1755449338739-6c4132cd6d47?crop=entropy&cs=srgb&fm=jpg&ixid=M3w2MDcyNjN8MHwxfHJhbmRvbXx8fHx8fHx8fDE3NTgxOTQwNzd8&ixlib=rb-4.1.0&q=85"
        ),
        new ChannelItem(
            "ch_4",
            "Data Weekly",
            "Практика по data engineering, BI и статистике.",
            88_100,
            31_200,
            "https://images.unsplash.com/photo-1754752603526-b4663b073344?crop=entropy&cs=srgb&fm=jpg&ixid=M3w2MDcyNjN8MHwxfHJhbmRvbXx8fHx8fHx8fDE3NTgxOTQwOTJ8&ixlib=rb-4.1.0&q=85"
        ),
        new ChannelItem(
            "ch_5",
            "Product Notes",
            "Короткие заметки про roadmap, discovery и delivery.",
            74_600,
            26_800,
            "https://images.unsplash.com/photo-1756394378625-9e5303619d6f?crop=entropy&cs=srgb&fm=jpg&ixid=M3w2MDcyNjN8MHwxfHJhbmRvbXx8fHx8fHx8fDE3NTgxOTQxMDR8&ixlib=rb-4.1.0&q=85"
        ),
        new ChannelItem(
            "ch_6",
            "Dev Ops Hub",
            "CI/CD, Kubernetes, observability и надежность.",
            112_900,
            45_500,
            "https://images.unsplash.com/photo-1755858434097-c410eee3e303?crop=entropy&cs=srgb&fm=jpg&ixid=M3w2MDcyNjN8MHwxfHJhbmRvbXx8fHx8fHx8fDE3NTgxOTQxMTZ8&ixlib=rb-4.1.0&q=85"
        ),
        new ChannelItem(
            "ch_7",
            "Mobile Craft",
            "Android/iOS разработка, производительность и UI.",
            69_700,
            24_100,
            "https://images.unsplash.com/photo-1755311904879-c109c5f1a405?crop=entropy&cs=srgb&fm=jpg&ixid=M3w2MDcyNjN8MHwxfHJhbmRvbXx8fHx8fHx8fDE3NTgxOTQxMjZ8&ixlib=rb-4.1.0&q=85"
        )
    );

    private final AnnotatedWidgetRegistry widgetRegistry;

    public ScreenDemoScreensController(AnnotatedWidgetRegistry widgetRegistry) {
        this.widgetRegistry = widgetRegistry;
    }

    record MenuEntry(String label, String targetScreen) {
    }

    public record ChannelItem(
        String id,
        String title,
        String description,
        int subscribers,
        int avgViews,
        String imageUrl
    ) {
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
            .line("Экран: CHANNELS")
            .line("Нажмите на канал для просмотра деталей")
            .build();
        WidgetView widgetView = widgetRegistry.render(CHANNELS_LIST_WIDGET, context, CHANNELS);
        widgetView.applyEffects(context);
        return widgetView.mergeInto(base);
    }

    @Screen(id = CATALOG_DETAILS_SCREEN)
    public ScreenView catalogDetails(ScreenContext context) {
        String selectedId = (String) context.screenState().getData("selected_channel_id").orElse(null);
        ChannelItem selected = CHANNELS.stream().filter(item -> item.id().equals(selectedId)).findFirst().orElse(null);
        InlineKeyboardMarkup keyboard = Keyboards.inlineKeyboard()
            .row(Keyboards.callbackButton("Назад", ScreenCallbackData.back()))
            .build();
        if (selected == null) {
            return ScreenView.builder()
                .line("Канал не найден")
                .replyMarkup(keyboard)
                .build();
        }
        return ScreenView.builder()
            .line("Детали канала")
            .line("ID: " + selected.id())
            .line("Название: " + selected.title())
            .line("Описание: " + selected.description())
            .line("Подписчики: " + selected.subscribers())
            .line("Средние просмотры: " + selected.avgViews())
            .line("Картинка:")
            .line(selected.imageUrl())
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

    @OnScreenCallback(screen = CATALOG_LIST_SCREEN, callbackPrefix = "w:channels_list:")
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
