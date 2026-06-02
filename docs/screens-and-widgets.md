# Screen API и widgets

## Что входит в screen API

В `core` есть runtime для экранной модели:

- `ScreenEngine`
- `ScreenRegistry`
- `Screen`
- `ScreenView`
- `ScreenAction`
- `ScreenNavigator`
- `ScreenStateStorage`
- `InMemoryScreenStateStorage`
- `ScreenMiddleware`

Во `spring-boot-starter` добавлены аннотации и registry для widgets.

## Когда использовать screens

Экранный API подходит, когда бот строится как последовательность экранов:

- один активный экран на чат
- переходы вперед/назад
- повторный render экрана
- отдельное состояние пользователя и состояние экрана

## Аннотации Spring Boot

Для экранов:

- `@ScreenController`
- `@Screen`
- `@OnScreenMessage`
- `@OnScreenCallback`

Для widgets:

- `@WidgetController`
- `@Widget`
- `@OnWidgetAction`

## Пример экрана

```java
import ru.tardyon.botframework.telegram.api.model.markup.Keyboards;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.screen.ScreenContext;
import ru.tardyon.botframework.telegram.screen.ScreenView;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnScreenCallback;
import ru.tardyon.botframework.telegram.spring.boot.annotation.Screen;
import ru.tardyon.botframework.telegram.spring.boot.annotation.ScreenController;

@ScreenController
public class SettingsScreenController {

    private static final String SETTINGS = "settings";
    private static final String TOGGLE = "screen:settings:toggle";

    @Screen(id = SETTINGS, main = true)
    public ScreenView settings(ScreenContext context) {
        boolean enabled = context.screenState().getData("notifications")
            .map(Boolean.class::cast)
            .orElse(false);

        return ScreenView.builder()
            .line("Экран настроек")
            .line("notifications=" + enabled)
            .replyMarkup(
                Keyboards.inlineKeyboard()
                    .row(Keyboards.callbackButton("Переключить", TOGGLE))
                    .build()
            )
            .build();
    }

    @OnScreenCallback(screen = SETTINGS, callbackEquals = TOGGLE)
    public ScreenAction toggle(ScreenContext context) {
        boolean enabled = context.screenState().getData("notifications")
            .map(Boolean.class::cast)
            .orElse(false);
        context.screenState().putData("notifications", !enabled);
        return ScreenAction.render();
    }
}
```

## Переходы между экранами

`ScreenAction` поддерживает:

- `handled()`
- `render()`
- `push(screenId)`
- `push(screenId, targetData)`
- `replace(screenId)`
- `replace(screenId, targetData)`
- `back()`
- `clear()`
- `unhandled()`

Это покрывает типичные переходы между экранами и очистку screen stack.

## Widgets

Widgets позволяют переиспользовать куски UI и callback-логики.

Пример:

```java
import java.util.List;
import ru.tardyon.botframework.telegram.screen.ScreenAction;
import ru.tardyon.botframework.telegram.spring.boot.widget.OnWidgetAction;
import ru.tardyon.botframework.telegram.spring.boot.widget.Widget;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetButtons;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetController;
import ru.tardyon.botframework.telegram.spring.boot.widget.WidgetView;

@WidgetController
public class MenuWidgetController {

    record MenuItem(String label, String target) {
    }

    @Widget(id = "home_menu")
    public WidgetView homeMenu(List<MenuItem> items) {
        return WidgetView.builder()
            .line("Меню")
            .replyMarkup(
                WidgetButtons.objectList(
                    "home_menu",
                    "open",
                    items,
                    MenuItem::label,
                    MenuItem::target
                )
            )
            .build();
    }

    @OnWidgetAction(widget = "home_menu", action = "open")
    public ScreenAction open(String payload) {
        return ScreenAction.push(payload);
    }
}
```

## Screen state storage

По умолчанию starter использует in-memory storage:

```yaml
telegram:
  bot:
    screen-state:
      storage: memory
```

При необходимости можно использовать Redis:

```yaml
telegram:
  bot:
    screen-state:
      storage: redis
      redis:
        key-prefix: telegram:screen
        ttl-seconds: 86400
```

## Demo-модуль

Практический пример экранов и widgets находится в модуле:

- `telegram-bot-framework-screen-demo`

Он показывает:

- главный экран
- push/back навигацию
- хранение screen state
- widget-based меню
- работу с `targetData`
