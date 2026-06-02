# Testkit и demo-модули

## `telegram-bot-framework-testkit`

`testkit` предназначен для интеграционных тестов без реального Telegram API.

Состав модуля:

- `FakeBotApiServer`
- `PollingUpdateSimulator`
- `WebhookSimulator`
- `RequestAssertions`
- `TelegramJsonFixtures`
- `UpdateFixtures`

## FakeBotApiServer

`FakeBotApiServer` поднимает локальный HTTP server и записывает запросы к Bot API.

Основные возможности:

- старт на случайном или фиксированном порту
- `baseUrl()` для подстановки в transport profile
- очередь ответов по имени Telegram method
- `setDefaultResponder(...)`
- `recordedRequests()`
- `recordedRequests("sendMessage")`
- интеграция с `PollingUpdateSimulator`

Пример:

```java
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiServer;
import ru.tardyon.botframework.telegram.testkit.server.FakeBotApiResponse;

try (FakeBotApiServer server = new FakeBotApiServer().start()) {
    server.enqueueResponse("sendMessage", FakeBotApiResponse.okBoolean());
    String baseUrl = server.baseUrl();
}
```

## PollingUpdateSimulator

`PollingUpdateSimulator` формирует ответ для `getUpdates` на основе очереди `Update`.

Возможности:

- `enqueue(update)`
- `enqueueAll(...)`
- `clear()`
- `size()`

Обычно используется вместе с `FakeBotApiServer.attachPollingSimulator(...)`.

## WebhookSimulator

`WebhookSimulator` отправляет webhook payload на локальный endpoint.

Возможности:

- `sendUpdate(webhookUrl, update)`
- `sendUpdate(webhookUrl, update, secretToken)`
- `sendJson(webhookUrl, jsonPayload)`
- `sendJson(webhookUrl, jsonPayload, secretToken)`

Это удобно для проверки webhook-сценариев и secret token validation.

## RequestAssertions и fixtures

Для тестов также доступны:

- `RequestAssertions` для проверок записанных Bot API запросов
- `TelegramJsonFixtures` с готовыми JSON fixtures
- `UpdateFixtures` для создания update-объектов

## Demo-модуль starter

`telegram-bot-framework-demo` показывает реальные сценарии использования starter.

Что есть в demo:

- polling
- webhook
- cloud transport
- local Bot API transport
- fake profile для локальной проверки через testkit
- FSM
- callback routing
- inline mode
- invoice
- web app
- media group
- menu button
- paid media, gifts и Stars
- business stories и checklist
- diagnostics listeners

Запуск demo в polling/cloud:

```bash
export BOT_TOKEN=123456:ABCDEF
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling,cloud'
```

Запуск demo в polling/local:

```bash
export BOT_TOKEN=123456:ABCDEF
export DEMO_LOCAL_BOTAPI_BASE_URL=http://127.0.0.1:8081
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=polling,local'
```

Запуск demo в webhook/cloud:

```bash
export BOT_TOKEN=123456:ABCDEF
export BOT_WEBHOOK_PUBLIC_URL=https://example.com
export BOT_WEBHOOK_SECRET_TOKEN=super-secret
./gradlew :telegram-bot-framework-demo:bootRun --args='--spring.profiles.active=webhook,cloud'
```

Локальная проверка fake-режима:

```bash
./gradlew :telegram-bot-framework-demo:test --tests '*DemoFakeModeIntegrationTest'
```

## Demo экранного API

`telegram-bot-framework-screen-demo` - отдельный пример для screen stack и widgets.

Запуск:

```bash
BOT_TOKEN=<your_token> ./gradlew :telegram-bot-framework-screen-demo:bootRun
```

Он нужен как reference-проект для screen API, а не как отдельная runtime-библиотека.
