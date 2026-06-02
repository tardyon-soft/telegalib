# Использование со Spring Boot

## Подключение

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.tardyon.botframework:telegram-bot-framework-spring-boot-starter:<version>")
}
```

Starter транзитивно подтягивает `telegram-bot-framework-core`.

## Что настраивает starter

Автоконфигурация поднимает:

- `TelegramApiClient`
- `BotApiTransportProfile`
- `LongPollingOptions`
- `LongPollingRunner`
- `Router`
- `Dispatcher`
- `TelegramBot`
- `TelegramBotLifecycle`
- `WebhookUpdateProcessor`
- `TelegramWebhookController` для servlet web application
- `StateStorage`
- `ScreenStateStorage`
- `ScreenRegistry`
- `ScreenEngine`
- `ScreenMiddleware`
- `TelegramMonetizationOperations`
- `TelegramBusinessOperations`

## Базовая конфигурация polling

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
      allowed-updates:
        - message
        - callback_query
```

## Local Bot API transport

```yaml
telegram:
  bot:
    transport:
      mode: local
      base-url: http://127.0.0.1:8081
      local-file-uri-upload-enabled: true
```

## Webhook mode

```yaml
telegram:
  bot:
    token: ${BOT_TOKEN}
    mode: webhook
    polling:
      enabled: false
    webhook:
      enabled: true
      path: /telegram/webhook
      public-url: ${BOT_WEBHOOK_PUBLIC_URL}
      secret-token: ${BOT_WEBHOOK_SECRET_TOKEN:}
      drop-pending-updates: true
```

Если указан `webhook.public-url`, starter вызывает `setWebhook` при старте приложения.

## Proxy

Поддерживаются HTTP и SOCKS5 proxy:

```yaml
telegram:
  bot:
    proxy:
      enabled: true
      type: socks5
      host: 127.0.0.1
      port: 1080
      username: ${PROXY_USER:}
      password: ${PROXY_PASSWORD:}
```

## Аннотационный стиль

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

Доступные аннотации обработчиков:

- `@OnMessage`
- `@OnCallbackQuery`
- `@OnInlineQuery`
- `@OnChosenInlineResult`
- `@OnMyChatMember`
- `@OnChatMember`
- `@OnShippingQuery`
- `@OnPreCheckoutQuery`
- `@OnBusinessConnection`
- `@OnBusinessMessage`
- `@OnEditedBusinessMessage`
- `@OnDeletedBusinessMessages`
- `@OnScreenMessage`
- `@OnScreenCallback`

## Router bean вместо аннотаций

Если удобнее собирать маршруты вручную, можно объявить `Router` как Spring bean:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tardyon.botframework.telegram.dispatcher.Router;
import ru.tardyon.botframework.telegram.dispatcher.filter.Filters;

@Configuration
public class BotRoutingConfiguration {

    @Bean
    Router router() {
        Router router = new Router();
        router.message(Filters.command("start"), (ctx, msg) -> ctx.telegramMessage().reply("Привет"));
        router.callbackQuery(Filters.callbackDataStartsWith("menu:"), (ctx, cbq) -> ctx.telegramCallbackQuery().answer("OK"));
        return router;
    }
}
```

## FSM storage

По умолчанию используется in-memory storage:

```yaml
telegram:
  bot:
    state:
      storage: memory
```

Можно переключить на Redis:

```yaml
telegram:
  bot:
    state:
      storage: redis
      redis:
        key-prefix: telegram:fsm
        ttl-seconds: 86400

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## Screen state storage

По умолчанию:

```yaml
telegram:
  bot:
    screen-state:
      storage: memory
```

Через Redis:

```yaml
telegram:
  bot:
    screen-state:
      storage: redis
      redis:
        key-prefix: telegram:screen
        ttl-seconds: 86400

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## Diagnostics

В starter можно зарегистрировать listeners обычными Spring beans:

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

И включить их через конфигурацию:

```yaml
telegram:
  bot:
    diagnostics:
      enabled: true
```

## Сервисы для monetization и business API

Starter регистрирует два thin-wrapper bean:

- `TelegramMonetizationOperations`
- `TelegramBusinessOperations`

Пример:

```java
import org.springframework.stereotype.Component;
import ru.tardyon.botframework.telegram.api.method.GetStarTransactionsRequest;
import ru.tardyon.botframework.telegram.spring.boot.service.TelegramMonetizationOperations;

@Component
public class StarsService {
    private final TelegramMonetizationOperations monetizationOperations;

    public StarsService(TelegramMonetizationOperations monetizationOperations) {
        this.monetizationOperations = monetizationOperations;
    }

    public void loadTransactions() {
        monetizationOperations.getStarTransactions(new GetStarTransactionsRequest(null, null));
    }
}
```

## Когда выбирать starter

Starter удобен, если:

- приложение уже работает на Spring Boot
- нужен готовый lifecycle polling/webhook
- хочется описывать обработчики аннотациями
- нужен Redis storage без ручной сборки runtime
