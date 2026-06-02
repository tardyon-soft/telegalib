# Модули и состав репозитория

## Основные модули

### `telegram-bot-framework-core`

Базовый runtime без Spring:

- `TelegramApiClient` и `DefaultTelegramApiClient`
- модели и request-объекты Telegram Bot API
- `LongPollingRunner`
- webhook runtime: `DefaultWebhookUpdateProcessor`, `JdkWebhookHttpServer`
- `Router`, `Filters`, `DefaultDispatcher`, `UpdateMiddleware`
- FSM: `StateStorage`, `InMemoryStateStorage`
- screen API: `ScreenEngine`, `ScreenRegistry`, `ScreenStateStorage`
- web app validator: `WebAppInitDataValidator`
- diagnostics hooks и listeners

### `telegram-bot-framework-spring-boot-starter`

Адаптер над `core` для Spring Boot:

- автоконфигурация клиента, dispatcher и polling/webhook runtime
- `@BotController` и аннотации обработчиков update-событий
- webhook controller для servlet web application
- конфигурация transport, proxy, diagnostics
- memory/redis-хранилище для FSM и screen state
- screen annotations и widget annotations
- сервисы `TelegramMonetizationOperations` и `TelegramBusinessOperations`

## Вспомогательные модули

### `telegram-bot-framework-testkit`

Модуль для тестов:

- `FakeBotApiServer`
- `PollingUpdateSimulator`
- `WebhookSimulator`
- `RequestAssertions`
- `TelegramJsonFixtures`
- `UpdateFixtures`

### `telegram-bot-framework-demo`

Spring Boot demo-приложение на базе starter. Показывает:

- polling и webhook
- cloud/local transport profile
- FSM
- inline mode
- invoice, web app, media group
- business и monetization сценарии
- diagnostics listeners

### `telegram-bot-framework-screen-demo`

Отдельный demo-модуль экранного API:

- `@ScreenController`
- `@Screen`
- `@OnScreenMessage`
- `@OnScreenCallback`
- widgets и widget actions

### `telegram-bot-framework-botapi-generator`

Внутренний tooling-модуль для генерации части DTO и method scaffolding по subset schema.

## Что публикуется

В Maven Central настроена публикация только двух модулей:

- `telegram-bot-framework-core`
- `telegram-bot-framework-spring-boot-starter`

`demo`, `screen-demo`, `testkit` и `botapi-generator` остаются внутри репозитория как вспомогательные модули.
