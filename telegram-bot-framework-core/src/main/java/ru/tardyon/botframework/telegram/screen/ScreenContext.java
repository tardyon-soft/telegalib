package ru.tardyon.botframework.telegram.screen;

import java.util.Objects;
import ru.tardyon.botframework.telegram.api.model.CallbackQuery;
import ru.tardyon.botframework.telegram.api.model.Message;
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.fsm.StateContext;

public final class ScreenContext {

    private final UpdateContext updateContext;
    private final ScreenStateContext screenStateContext;
    private final ScreenNavigator navigator;

    public ScreenContext(UpdateContext updateContext, ScreenStateContext screenStateContext, ScreenNavigator navigator) {
        this.updateContext = Objects.requireNonNull(updateContext, "updateContext must not be null");
        this.screenStateContext = Objects.requireNonNull(screenStateContext, "screenStateContext must not be null");
        this.navigator = Objects.requireNonNull(navigator, "navigator must not be null");
    }

    public UpdateContext updateContext() {
        return updateContext;
    }

    public Message message() {
        return updateContext.getMessage();
    }

    public CallbackQuery callbackQuery() {
        return updateContext.getCallbackQuery();
    }

    public TelegramMessage telegramMessage() {
        return updateContext.telegramMessage();
    }

    public TelegramCallbackQuery telegramCallbackQuery() {
        return updateContext.telegramCallbackQuery();
    }

    public ScreenStateContext screenState() {
        return screenStateContext;
    }

    public StateContext userState() {
        return updateContext.state();
    }

    public ScreenNavigator navigator() {
        return navigator;
    }
}
