# Маршрутизация, фильтры и FSM

## Router

`Router` - центральная точка маршрутизации update-событий.

Поддерживаемые группы маршрутов:

- `message(...)`
- `callbackQuery(...)`
- `inlineQuery(...)`
- `chosenInlineResult(...)`
- `myChatMember(...)`
- `chatMember(...)`
- `shippingQuery(...)`
- `preCheckoutQuery(...)`
- `businessConnection(...)`
- `businessMessage(...)`
- `editedBusinessMessage(...)`
- `deletedBusinessMessages(...)`

Есть два варианта регистрации:

- через обычный `Filter<E>`
- через `ContextFilter<E>`, если фильтр зависит от `UpdateContext`

## Пример Router

```java
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

Router router = new Router();

router.message(Filters.command("start"), (ctx, msg) -> ctx.telegramMessage().reply("Привет"));
router.message(Filters.textEquals("ping"), (ctx, msg) -> ctx.telegramMessage().reply("pong"));
router.callbackQuery(Filters.callbackDataStartsWith("menu:"), (ctx, cbq) -> ctx.telegramCallbackQuery().answer("OK"));
```

## Filters

В `Filters` уже есть готовые предикаты.

Для сообщений:

- `textPresent()`
- `textEquals(...)`
- `textStartsWith(...)`
- `command(...)`
- `commands(...)`
- `fromUser(...)`
- `fromChat(...)`
- `privateChat()`
- `groupChat()`
- `supergroupChat()`
- `channelChat()`

Для callback:

- `callbackDataPresent()`
- `callbackDataEquals(...)`
- `callbackDataStartsWith(...)`

Для платежей:

- `invoicePayloadEquals(...)`
- `preCheckoutPayloadEquals(...)`

Для состояния:

- `stateEquals(...)`
- `inStates(...)`
- `noState()`

## UpdateContext

`UpdateContext` передается в обработчики и содержит:

- текущий `Update`
- `TelegramApiClient`
- `StateStorage`
- bot id
- runtime attributes

Через него доступны state-операции и обертки:

- `ctx.telegramMessage()`
- `ctx.telegramCallbackQuery()`

## DefaultDispatcher и middleware

`DefaultDispatcher` запускает router и может применять цепочку `UpdateMiddleware`.

Пример:

```java
import java.util.List;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.middleware.ErrorBoundaryUpdateMiddleware;
import ru.tardyon.botframework.telegram.dispatcher.middleware.LoggingUpdateMiddleware;

DefaultDispatcher dispatcher = new DefaultDispatcher(
    router,
    List.of(
        new ErrorBoundaryUpdateMiddleware(),
        new LoggingUpdateMiddleware()
    )
);
```

Если нужна своя логика, реализуется интерфейс `UpdateMiddleware`.

## FSM

FSM в библиотеке строится вокруг:

- `State`
- `StateKey`
- `StateStorage`
- `InMemoryStateStorage`

Состояние привязано к пользователю и чату через `UpdateContext`.

## Пример диалога с FSM

```java
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;
import ru.tardyon.botframework.telegram.fsm.State;

Router router = new Router();

router.message(Filters.command("startform"), (ctx, msg) -> {
    ctx.state().setState(State.of("form.awaiting_name"));
    ctx.telegramMessage().reply("Введите имя");
});

router.message(Filters.stateEquals("form.awaiting_name"), (ctx, msg) -> {
    ctx.state().putData("name", msg.text());
    ctx.state().setState(State.of("form.awaiting_language"));
    ctx.telegramMessage().reply("Введите язык");
});

router.message(Filters.stateEquals("form.awaiting_language"), (ctx, msg) -> {
    Object name = ctx.state().getData("name").orElse("unknown");
    ctx.state().clear();
    ctx.telegramMessage().reply("Имя: " + name + ", язык: " + msg.text());
});
```

## Когда использовать FSM

FSM подходит, когда нужно:

- вести пошаговый диалог
- хранить промежуточные данные между сообщениями
- различать обработку одинакового текста в разных этапах сценария

Если диалог должен переживать рестарт приложения, в Spring Boot варианте можно включить Redis storage.
