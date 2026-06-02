# telegram-bot-framework

`telegram-bot-framework` - многомодульная Java-библиотека для разработки Telegram-ботов. Репозиторий собран на Java 21 и Gradle.

Публикуемые артефакты:

- `ru.tardyon.botframework:telegram-bot-framework-core`
- `ru.tardyon.botframework:telegram-bot-framework-spring-boot-starter`

Остальные модули в репозитории используются как demo, testkit или tooling.

## Что есть в библиотеке

- `TelegramApiClient` для прямой работы с Telegram Bot API.
- runtime для long polling и webhook.
- `Router`, фильтры и middleware для маршрутизации update-событий.
- FSM с `StateStorage`.
- screen API со стеком экранов и состоянием экранов.
- Spring Boot starter с автоконфигурацией, аннотациями и webhook-контроллером.
- testkit для интеграционных тестов.

## Подключение

### Vanilla Java

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.tardyon.botframework:telegram-bot-framework-core:<version>")
}
```

### Spring Boot

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.tardyon.botframework:telegram-bot-framework-spring-boot-starter:<version>")
}
```

## Быстрый старт

### Vanilla Java

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

        LongPollingRunner pollingRunner = new LongPollingRunner(client, LongPollingOptions.defaults());
        TelegramBot bot = new DefaultTelegramBot(pollingRunner, new DefaultDispatcher(router));

        bot.startPolling();
    }
}
```

### Spring Boot

`application.yml`:

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: polling
    transport:
      mode: cloud
    polling:
      enabled: true
      timeout: 30
      limit: 100
```

Контроллер:

```java
import ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery;
import ru.tardyon.botframework.telegram.bot.TelegramMessage;
import ru.tardyon.botframework.telegram.spring.boot.annotation.BotController;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnCallbackQuery;
import ru.tardyon.botframework.telegram.spring.boot.annotation.OnMessage;

@BotController
public class MyBotController {

    @OnMessage(command = "start")
    public void onStart(TelegramMessage message) {
        message.reply("Привет");
    }

    @OnMessage(textEquals = "ping")
    public void onPing(TelegramMessage message) {
        message.reply("pong");
    }

    @OnCallbackQuery(callbackPrefix = "menu:")
    public void onMenu(TelegramCallbackQuery callback) {
        callback.answer("OK");
    }
}
```

## Документация

- [Модули и состав репозитория](docs/modules.md)
- [Использование в vanilla Java](docs/vanilla-java.md)
- [Использование со Spring Boot](docs/spring-boot.md)
- [Маршрутизация, фильтры и FSM](docs/routing-and-fsm.md)
- [Screen API и widgets](docs/screens-and-widgets.md)
- [Testkit и demo-модули](docs/testkit-and-demo.md)

## Что выбрать

- Если нужен минимальный runtime без Spring, начните с [vanilla Java](docs/vanilla-java.md).
- Если приложение уже на Spring Boot, используйте [starter](docs/spring-boot.md).
- Если нужен пошаговый диалог или хранение пользовательского состояния, смотрите [Router, Filters и FSM](docs/routing-and-fsm.md).
- Если нужен экранный интерфейс поверх callback-кнопок, смотрите [Screen API и widgets](docs/screens-and-widgets.md).
- Если нужно тестировать интеграцию без реального Telegram API, используйте [testkit](docs/testkit-and-demo.md).
