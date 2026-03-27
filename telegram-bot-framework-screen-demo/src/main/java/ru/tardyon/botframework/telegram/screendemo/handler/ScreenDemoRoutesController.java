package ru.tardyon.botframework.telegram.screendemo.handler;

import java.util.Optional;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.dispatcher.UpdateContext;
import ru.tardyon.botframework.telegram.spring.boot.annotation.BotController;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnMessage;

@BotController
public class ScreenDemoRoutesController {

    @OnMessage(command = "user_state_set")
    public void onUserStateSet(UpdateContext context, TelegramMessage message) {
        context.state().putData("preferred_theme", "dark");
        message.reply("User state сохранен: preferred_theme=dark");
    }

    @OnMessage(command = "user_state_show")
    public void onUserStateShow(UpdateContext context, TelegramMessage message) {
        Optional<Object> value = context.state().getData("preferred_theme");
        message.reply("User state preferred_theme=" + value.orElse("<empty>"));
    }
}
