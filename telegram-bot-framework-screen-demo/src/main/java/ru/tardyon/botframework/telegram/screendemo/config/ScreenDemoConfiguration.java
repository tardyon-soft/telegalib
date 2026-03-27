package ru.tardyon.botframework.telegram.screendemo.config;

import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.api.model.markup.InlineKeyboardMarkup;
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;
import ru.tardyon.botframework.telegram.dispatcher.middleware.UpdateMiddleware;
import ru.tardyon.botframework.telegram.screen.InMemoryScreenStateStorage;
import ru.tardyon.botframework.telegram.screen.Screen;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenCallbackData;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.screen.ScreenEngine;
import ru.tardyon.botframework.telegram.screen.ScreenMiddleware;
import ru.tardyon.botframework.telegram.screen.ScreenRegistry;
import ru.tardyon.botframework.telegram.screen.ScreenStateStorage;
import ru.tardyon.botframework.telegram.screen.ScreenView;
import ru.tardyon.botframework.telegram.screen.Widgets;

@Configuration
public class ScreenDemoConfiguration {

    @Bean
    public ScreenStateStorage screenStateStorage() {
        return new InMemoryScreenStateStorage();
    }

    @Bean
    public ScreenRegistry screenRegistry() {
        ScreenRegistry registry = new ScreenRegistry();
        registry.register(new HomeScreen());
        registry.register(new SettingsScreen());
        registry.register(new ProfileScreen());
        return registry;
    }

    @Bean
    public ScreenEngine screenEngine(ScreenRegistry screenRegistry, ScreenStateStorage screenStateStorage) {
        return new ScreenEngine(screenRegistry, screenStateStorage);
    }

    @Bean
    public UpdateMiddleware screenMiddleware(ScreenEngine screenEngine) {
        return new ScreenMiddleware(screenEngine);
    }

    @Bean
    public Router telegramRouter(ScreenEngine screenEngine) {
        Router router = new Router();

        router.message(Filters.command("screen_start"), (context, message) -> screenEngine.start(context, HomeScreen.ID));

        router.message(Filters.command("user_state_set"), (context, message) -> {
            context.state().putData("preferred_theme", "dark");
            if (context.telegramMessage() != null) {
                context.telegramMessage().reply("User state сохранен: preferred_theme=dark");
            }
        });

        router.message(Filters.command("user_state_show"), (context, message) -> {
            Optional<Object> value = context.state().getData("preferred_theme");
            if (context.telegramMessage() != null) {
                context.telegramMessage().reply("User state preferred_theme=" + value.orElse("<empty>"));
            }
        });

        return router;
    }

    private static final class HomeScreen implements Screen {
        private static final String ID = "home";
        private static final String SETTINGS_CALLBACK = ScreenCallbackData.of("screen", "open", "settings");
        private static final String PROFILE_CALLBACK = ScreenCallbackData.of("screen", "open", "profile");

        @Override
        public String id() {
            return ID;
        }

        @Override
        public ScreenView render(ScreenContext context) {
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

        @Override
        public ScreenAction onMessage(ScreenContext context, Message message) {
            if ("back".equalsIgnoreCase(message.text())) {
                return ScreenAction.back();
            }
            return ScreenAction.unhandled();
        }

        @Override
        public ScreenAction onCallbackQuery(ScreenContext context, CallbackQuery callbackQuery) {
            if (context.telegramCallbackQuery() != null) {
                context.telegramCallbackQuery().answer();
            }
            String data = callbackQuery.data();
            if (SETTINGS_CALLBACK.equals(data)) {
                return ScreenAction.push(SettingsScreen.ID);
            }
            if (PROFILE_CALLBACK.equals(data)) {
                return ScreenAction.push(ProfileScreen.ID);
            }
            return ScreenAction.unhandled();
        }
    }

    private static final class SettingsScreen implements Screen {
        private static final String ID = "settings";
        private static final String TOGGLE_NOTIFICATIONS = ScreenCallbackData.of("screen", "toggle_notifications", "");

        @Override
        public String id() {
            return ID;
        }

        @Override
        public ScreenView render(ScreenContext context) {
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

        @Override
        public ScreenAction onCallbackQuery(ScreenContext context, CallbackQuery callbackQuery) {
            if (context.telegramCallbackQuery() != null) {
                context.telegramCallbackQuery().answer();
            }
            String data = callbackQuery.data();
            if (TOGGLE_NOTIFICATIONS.equals(data)) {
                boolean enabled = context.screenState().getData("notifications")
                    .map(Boolean.class::cast)
                    .orElse(false);
                context.screenState().putData("notifications", !enabled);
                return ScreenAction.render();
            }
            if (ScreenCallbackData.back().equals(data)) {
                return ScreenAction.back();
            }
            return ScreenAction.unhandled();
        }
    }

    private static final class ProfileScreen implements Screen {
        private static final String ID = "profile";

        @Override
        public String id() {
            return ID;
        }

        @Override
        public ScreenView render(ScreenContext context) {
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

        @Override
        public ScreenAction onCallbackQuery(ScreenContext context, CallbackQuery callbackQuery) {
            if (context.telegramCallbackQuery() != null) {
                context.telegramCallbackQuery().answer();
            }
            if (ScreenCallbackData.back().equals(callbackQuery.data())) {
                return ScreenAction.back();
            }
            return ScreenAction.unhandled();
        }
    }
}
