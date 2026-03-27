package ru.tardyon.botframework.telegram.screendemo.handler;

import java.util.List;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.spring.boot.widget.OnWidgetAction;
import ru.tardyon.botframework.telegram.spring.boot.widget.Widget;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetButtons;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetController;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetEffect;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetView;

@WidgetController
public class ScreenDemoWidgetsController {

    @Widget(id = "home_menu")
    public WidgetView homeMenu(List<ScreenDemoScreensController.MenuEntry> items) {
        return WidgetView.builder()
            .line("Меню")
            .replyMarkup(WidgetButtons.objectList("home_menu", "open", items, ScreenDemoScreensController.MenuEntry::label, ScreenDemoScreensController.MenuEntry::targetScreen))
            .build();
    }

    @OnWidgetAction(widget = "home_menu", action = "open")
    public ScreenAction openFromHomeMenu(String payload) {
        return ScreenAction.push(payload);
    }

    @Widget(id = "channels_list")
    public WidgetView channelsList(List<ScreenDemoScreensController.ChannelItem> items) {
        return WidgetView.builder()
            .line("Список каналов:")
            .replyMarkup(WidgetButtons.objectList("channels_list", "open", items,
                channel -> channel.title() + " • " + channel.subscribers() + " подписчиков",
                ScreenDemoScreensController.ChannelItem::id
            ))
            .effect(mediaHintEffect())
            .build();
    }

    @OnWidgetAction(widget = "channels_list", action = "open")
    public ScreenAction openChannel(ScreenContext context, String payload) {
        context.screenState().putData("selected_channel_id", payload);
        return ScreenAction.push("catalog_details");
    }

    private WidgetEffect mediaHintEffect() {
        return screenContext -> {
            if (screenContext.telegramMessage() != null) {
                // Widget effects can execute any runtime action, including extra media operations through API client.
            }
        };
    }
}
