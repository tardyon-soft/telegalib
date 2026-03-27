# telegram-bot-framework

Java Telegram Bot framework (multi-module, Java 21, Gradle).

- `groupId`: `ru.tardyon.botframework`
- base package: `ru.tardyon.botframework.telegram`

## Модули

- `telegram-bot-framework-core`
  - основной runtime (vanilla Java, без Spring)
- `telegram-bot-framework-spring-boot-starter`
  - thin Spring Boot adapter над core
- `telegram-bot-framework-demo`
  - пример приложения
- `telegram-bot-framework-screen-demo`
  - отдельный пример экранного API (screen stack + widgets)
- `telegram-bot-framework-botapi-generator`
  - tooling-only генератор DTO/method scaffolding
- `telegram-bot-framework-testkit`
  - testing-only fake Bot API server/simulators/assertions

## Что ставить в проект

Vanilla Java:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.tardyon.botframework:telegram-bot-framework-core:<version>")
}
```

Spring Boot:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.tardyon.botframework:telegram-bot-framework-spring-boot-starter:<version>")
}
```

`starter` подтягивает `core` транзитивно.

## Быстрый старт (vanilla Java)

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

## Spring Boot usage

### application.yml (polling + cloud)

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: polling
    transport:
      mode: cloud
      base-url: https://api.telegram.org
    polling:
      enabled: true
      timeout: 30
      limit: 100
```

### application.yml (polling + local Bot API)

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: polling
    transport:
      mode: local
      base-url: http://127.0.0.1:8081
      local-file-uri-upload-enabled: true
    polling:
      enabled: true
```

### application.yml (webhook)

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: webhook
    webhook:
      enabled: true
      path: /telegram/webhook
      public-url: ${BOT_WEBHOOK_PUBLIC_URL}
      secret-token: ${BOT_WEBHOOK_SECRET_TOKEN:}
      drop-pending-updates: true
```

### Annotation controller пример

```java
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
    public void onMenu(ru.tardyon.botframework.telegram.bot.TelegramCallbackQuery callback) {
        callback.answer("OK");
    }
}
```

## Diagnostics hooks

Если нужны diagnostics listeners в starter:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.diagnostics.BotApiRequestListener;
import ru.tardyon.botframework.telegram.diagnostics.BotApiResponseListener;

@Configuration
public class DiagnosticsConfig {
    @Bean
    BotApiRequestListener requestListener() {
        return event -> System.out.println("API -> " + event.methodName());
    }

    @Bean
    BotApiResponseListener responseListener() {
        return event -> System.out.println("API <- " + event.methodName() + " success=" + event.success());
    }
}
```

И включение в config:

```yaml
telegram:
  bot:
    diagnostics:
      enabled: true
```

## Demo

`telegram-bot-framework-demo` содержит готовые профили:

- `polling,cloud`
- `polling,local`
- `webhook,cloud`
- `fake` (test/dev режим через testkit)

Запуск:

```bash
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling,cloud'
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling,local'
./gradlew :telegram-bot-framework-demo:test --tests '*DemoFakeModeIntegrationTest'
```

`telegram-bot-framework-screen-demo`:

```bash
BOT_TOKEN=<your_token> ./gradlew :telegram-bot-framework-screen-demo:bootRun
```

## Публикация в Maven Central (через GitLab CI + JReleaser)

Публикуются только:

- `telegram-bot-framework-core`
- `telegram-bot-framework-spring-boot-starter`

Нужные переменные окружения (как в `.gitlab-ci.yml`):

- `RELEASE_VERSION`
- `JRELEASER_MAVENCENTRAL_USERNAME`
- `JRELEASER_MAVENCENTRAL_PASSWORD`
- `JRELEASER_GPG_PUBLIC_KEY`
- `JRELEASER_GPG_SECRET_KEY`
- `JRELEASER_GPG_PASSPHRASE`

Команда пайплайна:

```bash
./gradlew --no-daemon --stacktrace --no-configuration-cache clean publish jreleaserDeploy
```

`publish` складывает артефакты в `build/staging-deploy`, `jreleaserDeploy` отправляет staging в Maven Central Publisher API.

## Границы модулей

- `core` — production runtime/library logic
- `starter` — только wiring/autoconfiguration/lifecycle/annotation integration
- `demo` — только example app
- `generator` — tooling only
- `testkit` — testing support only
