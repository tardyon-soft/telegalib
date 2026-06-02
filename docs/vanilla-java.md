# Использование в vanilla Java

## Подключение

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.tardyon.botframework:telegram-bot-framework-core:<version>")
}
```

## Базовые компоненты

Для приложения без Spring обычно используются:

- `DefaultTelegramApiClient`
- `Router`
- `DefaultDispatcher`
- `LongPollingRunner`
- `DefaultTelegramBot`

## Пример long polling

```java
import ru.tardyon.botframework.telegram.api.DefaultTelegramApiClient;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.bot.DefaultTelegramBot;
import ru.tardyon.botframework.telegram.bot.TelegramBot;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;
import ru.tardyon.botframework.telegram.polling.LongPollingOptions;
import ru.tardyon.botframework.telegram.polling.LongPollingRunner;

public class VanillaBotMain {
    public static void main(String[] args) {
        TelegramApiClient client = new DefaultTelegramApiClient(System.getenv("BOT_TOKEN"));

        Router router = new Router();
        router.message(Filters.command("start"), (ctx, msg) -> ctx.telegramMessage().reply("Привет"));
        router.message(Filters.textEquals("ping"), (ctx, msg) -> ctx.telegramMessage().reply("pong"));
        router.callbackQuery(Filters.callbackDataStartsWith("menu:"), (ctx, cbq) -> ctx.telegramCallbackQuery().answer("OK"));

        LongPollingRunner pollingRunner = new LongPollingRunner(
            client,
            new LongPollingOptions(30, 100, null)
        );

        TelegramBot bot = new DefaultTelegramBot(pollingRunner, new DefaultDispatcher(router));
        bot.startPolling();
    }
}
```

`LongPollingOptions` позволяет задать:

- `timeout`
- `limit`
- `allowedUpdates`

## Пример webhook без Spring

В `core` есть минимальный HTTP adapter на JDK `HttpServer`.

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.tardyon.botframework.telegram.api.DefaultTelegramApiClient;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.dispatcher.DefaultDispatcher;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;
import ru.tardyon.botframework.telegram.webhook.DefaultWebhookUpdateProcessor;
import ru.tardyon.botframework.telegram.webhook.JdkWebhookHttpServer;

public class VanillaWebhookMain {
    public static void main(String[] args) throws Exception {
        TelegramApiClient client = new DefaultTelegramApiClient(System.getenv("BOT_TOKEN"));

        Router router = new Router();
        router.message(Filters.command("start"), (ctx, msg) -> ctx.telegramMessage().reply("Привет из webhook"));

        DefaultWebhookUpdateProcessor processor = new DefaultWebhookUpdateProcessor(
            new ObjectMapper(),
            new DefaultDispatcher(router),
            client,
            System.getenv("BOT_WEBHOOK_SECRET_TOKEN")
        );

        JdkWebhookHttpServer server = new JdkWebhookHttpServer(8080, "/telegram/webhook", processor);
        server.start();
    }
}
```

Если `expectedSecretToken` задан, `DefaultWebhookUpdateProcessor` проверяет заголовок `X-Telegram-Bot-Api-Secret-Token`.

## Работа с Router

`Router` поддерживает обработчики для update-типов:

- `message`
- `callbackQuery`
- `inlineQuery`
- `chosenInlineResult`
- `myChatMember`
- `chatMember`
- `shippingQuery`
- `preCheckoutQuery`
- `businessConnection`
- `businessMessage`
- `editedBusinessMessage`
- `deletedBusinessMessages`

Для каждого маршрута можно использовать обычный filter или context-aware filter.

## Фильтры

Готовые фильтры в `Filters`:

- `command(...)`
- `commands(...)`
- `textPresent()`
- `textEquals(...)`
- `textStartsWith(...)`
- `privateChat()`
- `groupChat()`
- `supergroupChat()`
- `channelChat()`
- `fromUser(...)`
- `fromChat(...)`
- `callbackDataEquals(...)`
- `callbackDataStartsWith(...)`
- `invoicePayloadEquals(...)`
- `preCheckoutPayloadEquals(...)`
- `stateEquals(...)`
- `inStates(...)`
- `noState()`

## Удобные обертки в обработчиках

В `UpdateContext` доступны обертки:

- `ctx.telegramMessage()`
- `ctx.telegramCallbackQuery()`

`TelegramMessage` умеет:

- `reply(...)`
- `editText(...)`
- `editReplyMarkup(...)`
- `delete()`
- `readAsBusiness()`
- `deleteAsBusiness()`

`TelegramCallbackQuery` умеет:

- `answer()`
- `answer(String text)`
- `message()`

## Прямой вызов Bot API

Если не нужен routing-слой, можно работать через `TelegramApiClient` напрямую:

```java
import ru.tardyon.botframework.telegram.api.DefaultTelegramApiClient;
import ru.tardyon.botframework.telegram.api.TelegramApiClient;
import ru.tardyon.botframework.telegram.api.method.SendMessageRequest;

public class DirectApiExample {
    public static void main(String[] args) {
        TelegramApiClient client = new DefaultTelegramApiClient(System.getenv("BOT_TOKEN"));
        client.sendMessage(new SendMessageRequest(123456789L, "Привет", null));
    }
}
```

Через `TelegramApiClient` в библиотеке реализованы, в частности:

- базовые сообщения, callback и inline API
- invoices и shipping/pre-checkout
- web app query
- media group и загрузка файлов
- chat/admin/member operations
- forum topics
- gifts, Stars, paid media
- business connection, stories и checklist

## Загрузка файлов

Для методов, принимающих `InputFile`, доступны варианты:

- `InputFile.fileId(...)`
- `InputFile.url(...)`
- `InputFile.path(...)`
- `InputFile.bytes(...)`
- `InputFile.stream(...)`

Это позволяет использовать как уже существующий `file_id`, так и локальный upload.

## Когда достаточно только `core`

`core` подходит, если:

- приложение без Spring Boot
- нужен полный контроль над запуском polling/webhook
- маршрутизацию и lifecycle удобнее собирать вручную
